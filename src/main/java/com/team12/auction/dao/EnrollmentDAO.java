package com.team12.auction.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.team12.auction.model.dto.EnrollmentDetail;
import com.team12.auction.util.DBConnection;

public class EnrollmentDAO {
    /**
     * 나의 등록 조회: 내가 수강 신청한 분반의 모든 속성과 강의 이름 반환
     */
    public List<EnrollmentDetail> getMyEnrollment(int studentId) throws SQLException {
        String sql = "SELECT s.section_id, s.section_number, s.professor, " +
                "s.capacity, s.classroom, s.course_id, " +
                "c.course_name, " +
                "e.enrollment_source, e.points_used " +
                "FROM Enrollment e " +
                "JOIN Section s ON e.section_id = s.section_id " +
                "JOIN Course c ON s.course_id = c.course_id " +
                "WHERE e.student_id = ? " +
                "ORDER BY s.section_id";

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<EnrollmentDetail> list = new ArrayList<>();

        try {
            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, studentId);

            rs = pstmt.executeQuery();

            while (rs.next()) {
                EnrollmentDetail detail = new EnrollmentDetail();
                detail.setSectionId(rs.getString(1));
                detail.setSectionNumber(rs.getInt(2));
                detail.setProfessor(rs.getString(3));
                detail.setCapacity(rs.getInt(4));
                detail.setClassroom(rs.getString(5));
                detail.setCourseId(rs.getString(6));
                detail.setCourseName(rs.getString(7));
                detail.setEnrollmentSource(rs.getString(8));
                detail.setPointsUsed(rs.getInt(9));
                list.add(detail);
            }

        } catch (SQLException e) {
            throw e;
        } finally {
            DBConnection.close(rs, pstmt, conn);
        }

        return list;
    }

    /**
     * 수강 신청 삭제
     */
    public int deleteEnrollment(int studentId, String sectionId) throws SQLException {
        String sql = "DELETE FROM Enrollment WHERE student_id = ? AND section_id = ?";

        Connection conn = null;
        PreparedStatement pstmt = null;
        int result = 0;

        try {
            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, studentId);
            pstmt.setString(2, sectionId);

            result = pstmt.executeUpdate();

            DBConnection.commit(conn);

        } catch (SQLException e) {
            DBConnection.rollback(conn);
            throw e;
        } finally {
            DBConnection.close(pstmt, conn);
        }

        return result;
    }

    /**
     * 학과별 수강인원 조회
     */
    public List<DepartmentEnrollmentStat> getEnrollmentByDepartment(String department) throws SQLException {
        String sql = "SELECT s.section_id, " +
                "c.course_name, " +
                "s.capacity AS section_capacity, " +
                "COUNT(e.student_id) AS enrolled_count " +
                "FROM Section s " +
                "JOIN Course c ON s.course_id = c.course_id " +
                "LEFT JOIN Enrollment e ON e.section_id = s.section_id " +
                "WHERE c.department = ? " +
                "GROUP BY s.section_id, c.course_name, s.capacity " +
                "ORDER BY c.course_name, s.section_id";

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<DepartmentEnrollmentStat> list = new ArrayList<>();

        try {
            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, department);

            rs = pstmt.executeQuery();

            while (rs.next()) {
                DepartmentEnrollmentStat stat = new DepartmentEnrollmentStat();
                stat.setSectionId(rs.getString(1));
                stat.setCourseName(rs.getString(2));
                stat.setSectionCapacity(rs.getInt(3));
                stat.setEnrolledCount(rs.getInt(4));
                list.add(stat);
            }

        } catch (SQLException e) {
            throw e;
        } finally {
            DBConnection.close(rs, pstmt, conn);
        }

        return list;
    }

    // DTO 클래스 추가 필요
    public static class DepartmentEnrollmentStat {
        private String sectionId;
        private String courseName;
        private int sectionCapacity;
        private int enrolledCount;

        // Getter & Setter
        public String getSectionId() { return sectionId; }
        public void setSectionId(String sectionId) { this.sectionId = sectionId; }

        public String getCourseName() { return courseName; }
        public void setCourseName(String courseName) { this.courseName = courseName; }

        public int getSectionCapacity() { return sectionCapacity; }
        public void setSectionCapacity(int sectionCapacity) { this.sectionCapacity = sectionCapacity; }

        public int getEnrolledCount() { return enrolledCount; }
        public void setEnrolledCount(int enrolledCount) { this.enrolledCount = enrolledCount; }
    }
}