package com.team12.auction.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class EnrollmentDAO extends BaseDao {

	public EnrollmentDAO(Connection conn) {
		super(conn);
	}

    // 1. 나의 등록 조회: 내가 수강 신청한 분반의 모든 속성과 강의 이름 출력
    public void printMyEnrollment(long studentId) throws SQLException {

        String sql =
            "SELECT s.section_id, s.section_number, s.professor, " +
            "       s.capacity, s.classroom, s.course_id, " +
            "       c.course_name, " +
            "       e.enrollment_source, e.points_used " +
            "FROM Enrollment e " +
            "JOIN Section s ON e.section_id = s.section_id " +
            "JOIN Course c ON s.course_id = c.course_id " +
            "WHERE e.student_id = ? " +
            "ORDER BY s.section_id";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, studentId);

            try (ResultSet rs = pstmt.executeQuery()) {

                if (!rs.isBeforeFirst()) {
                    System.out.println("현재 수강 신청된 분반이 없습니다.");
                    return;
                }

                System.out.printf(
                    "%-14s %-6s %-15s %-8s %-10s %-10s %-25s %-12s %-8s\n",
                    "SECTION_ID", "SEC_NO", "PROFESSOR", "CAP",
                    "CLASS", "COURSE_ID", "COURSE_NAME",
                    "SOURCE", "POINTS"
                );
                System.out.println(
                    "-----------------------------------------------------------------------------------------------------------------------------------------------"
                );

                while (rs.next()) {
                    String sectionId     = rs.getString(1);
                    int sectionNumber    = rs.getInt(2);
                    String professor     = rs.getString(3);
                    int capacity         = rs.getInt(4);
                    String classroom     = rs.getString(5);
                    String courseId      = rs.getString(6);
                    String courseName    = rs.getString(7);

                    String enrSource    = rs.getString(8);
                    int pointsUsed      = rs.getInt(9);

                    System.out.printf(
                        "%-14s %-6d %-15s %-8d %-10s %-10s %-25s %-12s %-8d\n",
                        sectionId, sectionNumber, professor, capacity,
                        classroom, courseId, courseName,
                        (enrSource != null ? enrSource : "-"),
                        pointsUsed
                    );
                }
            }
        }
    }
    
    
    public int deleteEnrollment(long studentId, String sectionId) throws SQLException {
        String sql = "DELETE FROM Enrollment WHERE student_id = ? AND section_id = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, studentId);
            pstmt.setString(2, sectionId);
            return pstmt.executeUpdate();
        }
    }


    public void printEnrollmentByDepartment(String department) throws SQLException {

    String sql =
        "SELECT s.section_id, " +
        "       c.course_name, " +
        "       s.capacity AS section_capacity, " +
        "       COUNT(e.student_id) AS enrolled_count " +
        "FROM Section s " +
        "JOIN Course c ON s.course_id = c.course_id " +
        "LEFT JOIN Enrollment e ON e.section_id = s.section_id " +
        "WHERE c.department = ? " +
        "GROUP BY s.section_id, c.course_name, s.capacity " +
        "ORDER BY c.course_name, s.section_id";

    try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
        pstmt.setString(1, department);

        try (ResultSet rs = pstmt.executeQuery()) {

            if (!rs.isBeforeFirst()) {
                System.out.println("해당 학과의 분반이 없습니다: " + department);
                return;
            }

            System.out.println("\n[학과별 수강인원 조회] - 학과: " + department);
            System.out.printf("%-14s %-25s %-10s %-15s\n",
                    "SECTION_ID", "COURSE_NAME", "CAPACITY", "ENROLLED_CNT");
            System.out.println("------------------------------------------------------------------");

            while (rs.next()) {
                String sectionId      = rs.getString(1);
                String courseName     = rs.getString(2);
                int sectionCapacity   = rs.getInt(3);
                int enrolledCount     = rs.getInt(4);

                System.out.printf("%-14s %-25s %-10d %-15d\n",
                        sectionId, courseName, sectionCapacity, enrolledCount);
            }
        }
    }
}
}
