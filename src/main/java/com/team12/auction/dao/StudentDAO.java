package com.team12.auction.dao;

import com.team12.auction.model.entity.Student;
import com.team12.auction.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class StudentDAO {
    /**
     * 학번과 비밀번호로 학생 인증
     */
    public Student login(int studentId, String password) throws SQLException {
        String sql = "SELECT student_id, name, department, grade, password, max_credits, max_point " +
            "FROM Student WHERE student_id = ? AND password = ?";

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        Student student = null;

        try {
            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, studentId);
            pstmt.setString(2, password);

            rs = pstmt.executeQuery();

            if (rs.next()) {
                student = new Student();
                student.setStudentId(rs.getInt(1));
                student.setName(rs.getString(2));
                student.setDepartment(rs.getString(3));
                student.setGrade(rs.getInt(4));
                student.setPassword(rs.getString(5));
                student.setMaxCredits(rs.getInt(6));
                student.setMaxPoint(rs.getInt(7));
            }

        } finally {
            DBConnection.close(rs, pstmt, conn);
        }

        return student;
    }

    /**
     * 같은 학번이 이미 있는지 확인
     */
    public boolean existsById(int studentId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM Student WHERE student_id = ?";

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        boolean exists = false;

        try {
            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, studentId);

            rs = pstmt.executeQuery();

            if (rs.next()) {
                int count = rs.getInt(1);
                exists = count > 0;
            }

        } finally {
            DBConnection.close(rs, pstmt, conn);
        }

        return exists;
    }

    /**
     * 회원가입(INSERT)
     */
    public int signUp(Student s) throws SQLException {
        String sql = "INSERT INTO Student " +
            "(student_id, name, department, grade, password, max_credits, max_point) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?)";

        Connection conn = null;
        PreparedStatement pstmt = null;
        int result = 0;

        try {
            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, s.getStudentId());
            pstmt.setString(2, s.getName());
            pstmt.setString(3, s.getDepartment());
            pstmt.setInt(4, s.getGrade());
            pstmt.setString(5, s.getPassword());
            pstmt.setInt(6, s.getMaxCredits());
            pstmt.setInt(7, s.getMaxPoint());

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
     * 학번으로 학생 조회
     */
    public Student selectById(int studentId) throws SQLException {
        String sql = "SELECT name, department, grade, password, max_credits, max_point " +
            "FROM Student WHERE student_id = ?";

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        Student student = null;

        try {
            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, studentId);

            rs = pstmt.executeQuery();

            if (rs.next()) {
                student = new Student();
                student.setStudentId(studentId);
                student.setName(rs.getString(1));
                student.setDepartment(rs.getString(2));
                student.setGrade(rs.getInt(3));
                student.setPassword(rs.getString(4));
                student.setMaxCredits(rs.getInt(5));
                student.setMaxPoint(rs.getInt(6));
            }

        } finally {
            DBConnection.close(rs, pstmt, conn);
        }

        return student;
    }

    /**
     * 현재 학점 조회
     */
    public int getCurrentCredits(int studentId) throws SQLException {
        String sql = "SELECT NVL(SUM(c.credits), 0) as total_credits " +
            "FROM Enrollment e " +
            "JOIN Section s ON e.section_id = s.section_id " +
            "JOIN Course c ON s.course_id = c.course_id " +
            "WHERE e.student_id = ?";

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        int credits = 0;

        try {
            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement(sql);

            pstmt.setInt(1, studentId);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                credits = rs.getInt(1);
            }
        } finally {
            DBConnection.close(rs, pstmt, conn);
        }

        return credits;
    }

    /**
     * 현재 포인트 조회
     */
    public int getCurrentPoints(int studentId) throws SQLException {
        String sql = "SELECT s.max_point - NVL(SUM(e.points_used), 0) as current_points " +
            "FROM Student s " +
            "LEFT JOIN Enrollment e ON s.student_id = e.student_id " +
            "WHERE s.student_id = ? " +
            "GROUP BY s.student_id, s.max_point";

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        int points = 0;

        try {
            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement(sql);

            pstmt.setInt(1, studentId);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                points = rs.getInt(1);
            }
        } finally {
            DBConnection.close(rs, pstmt, conn);
        }
        return points;
    }

    public boolean updateStudent(Student student) throws SQLException {
        String sql = "UPDATE Student SET name = ?, department = ?, grade = ?, " +
            "max_credits = ?, max_point = ? " +
            "WHERE student_id = ?";

        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement(sql);

            pstmt.setString(1, student.getName());
            pstmt.setString(2, student.getDepartment());
            pstmt.setInt(3, student.getGrade());
            pstmt.setInt(4, student.getMaxCredits());
            pstmt.setInt(5, student.getMaxPoint());
            pstmt.setInt(6, student.getStudentId());

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            conn.rollback();
            throw e;
        } finally {
            DBConnection.close(pstmt, conn);
        }
    }

    public boolean validateLogin(int studentId, String password) throws SQLException {
        String sql = "SELECT student_id " +
            "FROM Student " +
            "WHERE student_id = ? AND password = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, studentId);
            pstmt.setString(2, password);

            ResultSet rs = pstmt.executeQuery();

            return rs.next();
        }
    }


    public boolean changePassword(int studentId, String newPassword) throws SQLException {
        String sql = "UPDATE Student SET password = ? WHERE student_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, newPassword);
            pstmt.setInt(2, studentId);

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        }
    }

    /**
     * 학번으로 학생 조회 (selectById의 별칭)
     */
    public Student selectByStudentId(int studentId) throws SQLException {
        return selectById(studentId);
    }
}
