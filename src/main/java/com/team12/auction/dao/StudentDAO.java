package com.team12.auction.dao;

import java.sql.*;

import com.team12.auction.model.entity.Student;

public class StudentDAO extends BaseDao {
    public StudentDAO(Connection conn) {
       super(conn);
    }

    
 // 1) 로그인: 학번과 비밀번호가 일치하면 Student 객체 리턴, 아니면 null 리턴
    public Student login(long studentId, String password) throws SQLException {
        String sql = "SELECT student_id, name, department, grade, password, max_credits, max_point " +
                     "FROM Student WHERE student_id = ? AND password = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, studentId);
            pstmt.setString(2, password);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Student s = new Student();
                    s.setStudentId(rs.getInt(1));
                    s.setName(rs.getString(2));
                    s.setDepartment(rs.getString(3));
                    s.setGrade(rs.getInt(4));
                    s.setPassword(rs.getString(5));
                    s.setMaxCredits(rs.getInt(6));
                    s.setMaxPoint(rs.getInt(7));
                    return s;   // 로그인 성공
                } else {
                    return null; // 로그인 실패
                }
            }
        }
    }

    // 2) 같은 학번이 이미 있는지 확인
    public boolean existsById(long studentId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM Student WHERE student_id = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, studentId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    int count = rs.getInt(1);
                    return count > 0;
                }
            }
        }
        return false;
    }
    
    // 3) 회원가입(INSERT)
    public int signUp(Student s) throws SQLException {
        String sql = "INSERT INTO Student " +
                     "(student_id, name, department, grade, password) " +
                     "VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, s.getStudentId());
            pstmt.setString(2, s.getName());
            pstmt.setString(3, s.getDepartment());
            pstmt.setInt(4, s.getGrade());
            pstmt.setString(5, s.getPassword());

            return pstmt.executeUpdate();  // 1이면 성공
        }
    }
    
    /**
     * 학번으로 학생 조회
     */
    public Student selectById(int studentId) throws SQLException {
        String sql = 
            "SELECT name, department, grade, password, max_credits, max_point " +
            "FROM STUDENT " +
            "WHERE student_id = ?";
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, studentId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                Student student = new Student();
                student.setStudentId(studentId);
                student.setName(rs.getString(1));
                student.setDepartment(rs.getString(2));
                student.setGrade(rs.getInt(3));
                student.setPassword(rs.getString(4));
                student.setMaxCredits(rs.getInt(5));
                student.setMaxPoint(rs.getInt(6));
                rs.close();
                return student;
            }
            rs.close();
            return null;
        }
    }
}