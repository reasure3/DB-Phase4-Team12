package com.team12.auction.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.team12.auction.model.dto.SectionSearchResult;
import com.team12.auction.model.entity.Course;
import com.team12.auction.model.entity.Section;
import com.team12.auction.util.DBConnection;

public class SectionDAO {
	/**
	 * 강의 코드로 분반 조회
	 */
	public List<Section> selectByCourseId(String courseId) throws SQLException {
		String sql = "SELECT s.section_id, s.section_number, s.professor, s.capacity, s.classroom " + "FROM Section s "
				+ "JOIN Course c ON s.course_id = c.course_id " + "WHERE s.course_id = ? "
				+ "ORDER BY s.section_number";

		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		List<Section> sections = new ArrayList<>();

		try {
			conn = DBConnection.getConnection();
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, courseId);

			rs = pstmt.executeQuery();

			while (rs.next()) {
				Section section = new Section();
				section.setSectionId(rs.getString(1));
				section.setSectionNumber(rs.getInt(2));
				section.setProfessor(rs.getString(3));
				section.setCapacity(rs.getInt(4));
				section.setClassroom(rs.getString(5));
				section.setCourseId(courseId);
				sections.add(section);
			}

		} catch (SQLException e) {
			throw e;
		} finally {
			DBConnection.close(rs, pstmt, conn);
		}

		return sections;
	}

	/**
	 * 나의 분반 조회 (각 강의 당 한 분반만 등록 가능하다고 가정)
	 */
	public Map<Course, Section> selectMySection(int studentId) throws SQLException {
		String sql = "SELECT DISTINCT " + "s.section_id, s.section_number, s.professor, s.capacity, s.classroom, "
				+ "c.course_id, c.course_name, c.department, c.credits, c.semester, c.year " + "FROM Enrollment e "
				+ "JOIN Section s ON e.section_id = s.section_id " + "JOIN Course c ON s.course_id = c.course_id "
				+ "WHERE e.student_id = ? " + "ORDER BY c.course_id, s.section_number";

		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		Map<Course, Section> courseSectionMap = new HashMap<>();

		try {
			conn = DBConnection.getConnection();
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, studentId);

			rs = pstmt.executeQuery();

			while (rs.next()) {
				// Section 객체 생성
				Section section = new Section();
				section.setSectionId(rs.getString(1));
				section.setSectionNumber(rs.getInt(2));
				section.setProfessor(rs.getString(3));
				section.setCapacity(rs.getInt(4));
				section.setClassroom(rs.getString(5));

				// Course 객체 생성
				Course course = new Course();
				course.setCourseId(rs.getString(6));
				course.setCourseName(rs.getString(7));
				course.setDepartment(rs.getString(8));
				course.setCredits(rs.getInt(9));
				course.setSemester(rs.getString(10));
				course.setYear(rs.getInt(11));

				// Map에 추가
				courseSectionMap.put(course, section);
			}

		} catch (SQLException e) {
			throw e;
		} finally {
			DBConnection.close(rs, pstmt, conn);
		}

		return courseSectionMap;
	}

	/**
	 * 분반의 현재 등록 인원 조회
	 */
	public int getCurrentEnrollment(String sectionId) throws SQLException {
		String sql = "SELECT COUNT(*) AS cnt " + "FROM Enrollment " + "WHERE section_id = ?";

		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		int count = 0;

		try {
			conn = DBConnection.getConnection();
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, sectionId);

			rs = pstmt.executeQuery();

			if (rs.next()) {
				count = rs.getInt(1);
			}

		} catch (SQLException e) {
			throw e;
		} finally {
			DBConnection.close(rs, pstmt, conn);
		}

		return count;
	}

	/**
	 * 강의/분반 검색 (키워드, 학과 필터)
	 */
	public List<SectionSearchResult> searchSections(String keyword, String department) throws SQLException {
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT c.course_id, c.course_name, c.department, c.credits, ");
		sql.append("s.section_id, s.section_number, s.professor, s.capacity, s.classroom, ");
		sql.append("NVL(enrolled.enrolled_count, 0) AS enrolled_count, ");
		sql.append("NVL(basket.basket_count, 0) AS basket_count ");
		sql.append("FROM Section s ");
		sql.append("JOIN Course c ON s.course_id = c.course_id ");
		sql.append(
				"LEFT JOIN (SELECT section_id, COUNT(*) AS enrolled_count FROM Enrollment GROUP BY section_id) enrolled ");
		sql.append("ON enrolled.section_id = s.section_id ");
		sql.append(
				"LEFT JOIN (SELECT section_id, COUNT(*) AS basket_count FROM BasketItem GROUP BY section_id) basket ");
		sql.append("ON basket.section_id = s.section_id WHERE 1=1 ");

		List<Object> params = new ArrayList<>();

		if (keyword != null && !keyword.trim().isEmpty()) {
			sql.append("AND (LOWER(c.course_name) LIKE ? OR LOWER(s.professor) LIKE ? OR LOWER(c.course_id) LIKE ?) ");
			String likeKeyword = "%" + keyword.trim().toLowerCase() + "%";
			params.add(likeKeyword);
			params.add(likeKeyword);
			params.add(likeKeyword);
		}

		if (department != null && !department.trim().isEmpty()) {
			sql.append("AND LOWER(c.department) LIKE ? ");
			params.add("%" + department.trim().toLowerCase() + "%");
		}

		sql.append("ORDER BY c.course_id, s.section_number");

		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		List<SectionSearchResult> result = new ArrayList<>();

		try {
			conn = DBConnection.getConnection();
			pstmt = conn.prepareStatement(sql.toString());

			for (int i = 0; i < params.size(); i++) {
				pstmt.setObject(i + 1, params.get(i));
			}

			rs = pstmt.executeQuery();

			while (rs.next()) {
				SectionSearchResult item = new SectionSearchResult();
				item.setCourseId(rs.getString(1));
				item.setCourseName(rs.getString(2));
				item.setDepartment(rs.getString(3));
				item.setCredits(rs.getInt(4));
				item.setSectionId(rs.getString(5));
				item.setSectionNumber(rs.getInt(6));
				item.setProfessor(rs.getString(7));
				item.setCapacity(rs.getInt(8));
				item.setClassroom(rs.getString(9));
				item.setEnrolledCount(rs.getInt(10));
				item.setBasketCount(rs.getInt(11));
				result.add(item);
			}

		} catch (SQLException e) {
			throw e;
		} finally {
			DBConnection.close(rs, pstmt, conn);
		}

		return result;
	}

	/**
	 * 분반 ID로 해당 과목의 학점 조회
	 */
	public int getCourseCredits(String sectionId) throws SQLException {
		String sql = "SELECT c.credits " +
				"FROM Section s " +
				"JOIN Course c ON s.course_id = c.course_id " +
				"WHERE s.section_id = ?";

		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		int credits = 0;

		try {
			conn = DBConnection.getConnection();
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, sectionId);

			rs = pstmt.executeQuery();

			if (rs.next()) {
				credits = rs.getInt(1);
			}

		} catch (SQLException e) {
			throw e;
		} finally {
			DBConnection.close(rs, pstmt, conn);
		}

		return credits;
	}
}