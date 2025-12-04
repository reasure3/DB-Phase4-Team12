package com.team12.auction.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.team12.auction.model.entity.Course;
import com.team12.auction.util.DBConnection;

public class CourseDAO {
    /**
     * 강의 ID로 강의 조회
     */
    public Course selectById(String courseId) throws SQLException {
        String sql = "SELECT course_id, course_name, department, credits, capacity, semester, year " +
                "FROM Course WHERE course_id = ?";

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        Course course = null;

        try {
            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, courseId);

            rs = pstmt.executeQuery();

            if (rs.next()) {
                course = new Course();
                course.setCourseId(rs.getString(1));
                course.setCourseName(rs.getString(2));
                course.setDepartment(rs.getString(3));
                course.setCredits(rs.getInt(4));
                course.setCapacity(rs.getInt(5));
                course.setSemester(rs.getString(6));
                course.setYear(rs.getInt(7));
            }

        } catch (SQLException e) {
            throw e;
        } finally {
            DBConnection.close(rs, pstmt, conn);
        }

        return course;
    }

    /**
     * 전체 강의 리스트 반환
     */
    public List<Course> getAllCourses() throws SQLException {
        String sql = "SELECT course_id, course_name, department, credits, " +
                "capacity, semester, year " +
                "FROM Course " +
                "ORDER BY course_id";

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<Course> list = new ArrayList<>();

        try {
            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                Course c = new Course();
                c.setCourseId(rs.getString(1));
                c.setCourseName(rs.getString(2));
                c.setDepartment(rs.getString(3));
                c.setCredits(rs.getInt(4));
                c.setCapacity(rs.getInt(5));
                c.setSemester(rs.getString(6));
                c.setYear(rs.getInt(7));
                list.add(c);
            }

        } catch (SQLException e) {
            throw e;
        } finally {
            DBConnection.close(rs, pstmt, conn);
        }

        return list;
    }

    /**
     * 특정 학생이 수강 중인 강의 리스트 반환
     */
    public List<Course> getMyCourses(int studentId) throws SQLException {
        String sql = "SELECT DISTINCT c.course_id, c.course_name, c.department, c.credits, " +
                "c.capacity, c.semester, c.year " +
                "FROM Enrollment e " +
                "JOIN Section s ON e.section_id = s.section_id " +
                "JOIN Course c ON s.course_id = c.course_id " +
                "WHERE e.student_id = ? " +
                "ORDER BY c.course_id";

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<Course> list = new ArrayList<>();

        try {
            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, studentId);

            rs = pstmt.executeQuery();

            while (rs.next()) {
                Course c = new Course();
                c.setCourseId(rs.getString(1));
                c.setCourseName(rs.getString(2));
                c.setDepartment(rs.getString(3));
                c.setCredits(rs.getInt(4));
                c.setCapacity(rs.getInt(5));
                c.setSemester(rs.getString(6));
                c.setYear(rs.getInt(7));
                list.add(c);
            }

        } catch (SQLException e) {
            throw e;
        } finally {
            DBConnection.close(rs, pstmt, conn);
        }

        return list;
    }

    /**
     * 학과별 강의 조회
     */
    public List<Course> getCoursesByDepartment(String department) throws SQLException {
        String sql = "SELECT course_id, course_name, department, credits, " +
                "capacity, semester, year " +
                "FROM Course " +
                "WHERE department = ? " +
                "ORDER BY course_id";

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<Course> list = new ArrayList<>();

        try {
            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, department);

            rs = pstmt.executeQuery();

            while (rs.next()) {
                Course c = new Course();
                c.setCourseId(rs.getString(1));
                c.setCourseName(rs.getString(2));
                c.setDepartment(rs.getString(3));
                c.setCredits(rs.getInt(4));
                c.setCapacity(rs.getInt(5));
                c.setSemester(rs.getString(6));
                c.setYear(rs.getInt(7));
                list.add(c);
            }

        } catch (SQLException e) {
            throw e;
        } finally {
            DBConnection.close(rs, pstmt, conn);
        }

        return list;
    }
}