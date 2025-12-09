package com.team12.auction.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.team12.auction.model.dto.BasketItemDetail;
import com.team12.auction.util.DBConnection;

public class BasketDAO {
	/**
	 * 학생의 장바구니가 없으면 생성 (basket_id = 'B' + studentId)
	 */
	public void ensureBasketExists(int studentId) throws SQLException {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {
			conn = DBConnection.getConnection();

			// 1. 이 학생의 장바구니가 이미 있는지 확인
			String selectSql = "SELECT basket_id FROM Basket WHERE student_id = ?";
			pstmt = conn.prepareStatement(selectSql);
			pstmt.setInt(1, studentId);
			rs = pstmt.executeQuery();

			if (rs.next()) {
				// 이미 장바구니가 있으면, 기존 basket_id 는 그대로 두고 바로 종료
				return;
			}
			rs.close();
			pstmt.close();

			// 2. 장바구니가 없다면 새로 생성
			String studentIdStr = String.valueOf(studentId); // 예: 2025111482
			if (studentIdStr.length() > 9) {
				studentIdStr = studentIdStr.substring(0, 9); // 앞 9자리만 사용 -> 202511148
			}
			String newBasketId = "B" + studentIdStr; // -> B202511148

			String insertSql = "INSERT INTO Basket (basket_id, student_id) VALUES (?, ?)";
			pstmt = conn.prepareStatement(insertSql);
			pstmt.setString(1, newBasketId);
			pstmt.setInt(2, studentId);
			pstmt.executeUpdate();

			DBConnection.commit(conn);

		} catch (SQLException e) {
			DBConnection.rollback(conn);
			throw e;
		} finally {
			DBConnection.close(rs, pstmt, conn);
		}
	}

	/**
	 * 해당 분반이 이미 장바구니에 있는지 확인
	 */
	public boolean isSectionInBasket(int studentId, String sectionId) throws SQLException {
		String basketId = getBasketId(studentId);
		if (basketId == null) {
			return false;
		}
		String sql = "SELECT COUNT(*) " + "FROM BasketItem " + "WHERE basket_id = ? AND section_id = ?";

		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		boolean exists = false;

		try {
			conn = DBConnection.getConnection();
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, basketId);
			pstmt.setString(2, sectionId);

			rs = pstmt.executeQuery();

			if (rs.next()) {
				exists = rs.getInt(1) > 0;
			}

		} catch (SQLException e) {
			throw e;
		} finally {
			DBConnection.close(rs, pstmt, conn);
		}

		return exists;
	}

	/**
	 * 현재 장바구니에 담긴 과목들의 총 학점 계산
	 */
	public int getTotalCreditsInBasket(int studentId) throws SQLException {
		String basketId = getBasketId(studentId);
		if (basketId == null) {
			return 0;
		}

		String sql = "SELECT NVL(SUM(c.credits), 0) AS total_credits " +
				"FROM BasketItem bi " +
				"JOIN Section s ON bi.section_id = s.section_id " +
				"JOIN Course c ON s.course_id = c.course_id " +
				"WHERE bi.basket_id = ?";

		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		int totalCredits = 0;

		try {
			conn = DBConnection.getConnection();
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, basketId);

			rs = pstmt.executeQuery();

			if (rs.next()) {
				totalCredits = rs.getInt(1);
			}

		} catch (SQLException e) {
			throw e;
		} finally {
			DBConnection.close(rs, pstmt, conn);
		}

		return totalCredits;
	}

	/**
	 * 장바구니에 분반 추가 (정원 제한 없음)
	 */
	public void addSectionToBasket(int studentId, String sectionId) throws SQLException {
		String basketId = getBasketId(studentId);
		if (basketId == null) {
			throw new SQLException("장바구니 ID를 찾을 수 없습니다.");
		}

		Connection conn = null;
		PreparedStatement pstmt = null;

		try {
			conn = DBConnection.getConnection();

			// BasketItem에 INSERT (정원 체크 없이 담기)
			String sql = "INSERT INTO BasketItem "
					+ "(registration_time, status, processed_time, reason, basket_id, section_id) "
					+ "VALUES (SYSDATE, 'PENDING', NULL, NULL, ?, ?)";

			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, basketId);
			pstmt.setString(2, sectionId);
			pstmt.executeUpdate();

			DBConnection.commit(conn);

		} catch (SQLException e) {
			DBConnection.rollback(conn);
			throw e;
		} finally {
			DBConnection.close(pstmt, conn);
		}
	}

	/**
	 * 로그인한 학생의 수강꾸러미 조회
	 */
	public List<BasketItemDetail> getMyBasket(int studentId) throws SQLException {
		String sql = "SELECT s.section_id, s.section_number, s.professor, "
				+ "s.capacity, s.classroom, s.course_id, c.course_name, c.credits, "
				+ "bi.status, bi.reason, bi.registration_time, bi.processed_time, "
				+ "NVL(basket.basket_count, 0) AS basket_count " + "FROM Basket b "
				+ "JOIN BasketItem bi ON b.basket_id = bi.basket_id "
				+ "JOIN Section s ON bi.section_id = s.section_id " + "JOIN Course c ON s.course_id = c.course_id "
				+ "LEFT JOIN (SELECT section_id, COUNT(*) AS basket_count FROM BasketItem GROUP BY section_id) basket "
				+ "ON basket.section_id = s.section_id " + "WHERE b.student_id = ? "
				+ "ORDER BY bi.registration_time DESC";

		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		List<BasketItemDetail> list = new ArrayList<>();

		try {
			conn = DBConnection.getConnection();
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, studentId);

			rs = pstmt.executeQuery();

			while (rs.next()) {
				BasketItemDetail item = new BasketItemDetail();
				item.setSectionId(rs.getString(1));
				item.setSectionNumber(rs.getInt(2));
				item.setProfessor(rs.getString(3));
				item.setCapacity(rs.getInt(4));
				item.setClassroom(rs.getString(5));
				item.setCourseId(rs.getString(6));
				item.setCourseName(rs.getString(7));
				item.setCredits(rs.getInt(8));
				item.setStatus(rs.getString(9));
				item.setReason(rs.getString(10));
				item.setRegistrationTime(rs.getTimestamp(11));
				item.setProcessedTime(rs.getTimestamp(12));
				item.setBasketCount(rs.getInt(13));
				list.add(item);
			}

		} catch (SQLException e) {
			throw e;
		} finally {
			DBConnection.close(rs, pstmt, conn);
		}

		return list;
	}

	/**
	 * 학생의 basket_id 조회
	 */
	public String getBasketId(int studentId) throws SQLException {
		String sql = "SELECT basket_id FROM Basket WHERE student_id = ?";

		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String basketId = null;

		try {
			conn = DBConnection.getConnection();
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, studentId);

			rs = pstmt.executeQuery();

			if (rs.next()) {
				basketId = rs.getString(1);
			}

		} catch (SQLException e) {
			throw e;
		} finally {
			DBConnection.close(rs, pstmt, conn);
		}

		return basketId;
	}

	/**
	 * 장바구니에서 분반 삭제 (Enrollment까지 함께 제거)
	 */
	public int deleteSectionFromBasket(int studentId, String sectionId) throws SQLException {
		String basketId = getBasketId(studentId);
		if (basketId == null)
			return 0;

		Connection conn = null;
		PreparedStatement pstmt = null;
		int deletedItems = 0;

		try {
			conn = DBConnection.getConnection();

			String deleteItemSql = "DELETE FROM BasketItem WHERE basket_id = ? AND section_id = ?";
			pstmt = conn.prepareStatement(deleteItemSql);
			pstmt.setString(1, basketId);
			pstmt.setString(2, sectionId);
			deletedItems = pstmt.executeUpdate();
			pstmt.close();

//            String deleteEnrollmentSql = "DELETE FROM Enrollment WHERE student_id = ? AND section_id = ?";
//            pstmt = conn.prepareStatement(deleteEnrollmentSql);
//            pstmt.setInt(1, studentId);
//            pstmt.setString(2, sectionId);
//            pstmt.executeUpdate();
//            pstmt.close();

			DBConnection.commit(conn);

		} catch (SQLException e) {
			DBConnection.rollback(conn);
			throw e;
		} finally {
			DBConnection.close(pstmt, conn);
		}

		return deletedItems;
	}
}