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
		String sql = "SELECT student_id, name, department, grade, password, max_credits, max_point "
				+ "FROM Student WHERE student_id = ? AND password = ?";

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

		} catch (SQLException e) {
			throw e;
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

		} catch (SQLException e) {
			throw e;
		} finally {
			DBConnection.close(rs, pstmt, conn);
		}

		return exists;
	}

	/**
	 * 회원가입(INSERT)
	 */
	public int signUp(Student s) throws SQLException {
		String sql = "INSERT INTO Student " + "(student_id, name, department, grade, password, max_credits, max_point) "
				+ "VALUES (?, ?, ?, ?, ?, ?, ?)";

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
		String sql = "SELECT name, department, grade, password, max_credits, max_point "
				+ "FROM Student WHERE student_id = ?";

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

		} catch (SQLException e) {
			throw e;
		} finally {
			DBConnection.close(rs, pstmt, conn);
		}

		return student;
	}

	/**
	 * 학번으로 학생 조회 (selectById의 별칭)
	 */
	public Student selectByStudentId(int studentId) throws SQLException {
		return selectById(studentId);
	}
}