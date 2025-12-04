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
     * 학생의 장바구니가 없으면 생성 (basket_id = studentId)
     */
    public void ensureBasketExists(int studentId) throws SQLException {
        String basketId = String.valueOf(studentId);

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();

            // 1. 존재 여부 확인
            String checkSql = "SELECT COUNT(*) FROM Basket WHERE basket_id = ?";
            pstmt = conn.prepareStatement(checkSql);
            pstmt.setString(1, basketId);
            rs = pstmt.executeQuery();

            if (rs.next() && rs.getInt(1) > 0) {
                return; // 이미 있으니까 생성 안 함
            }
            rs.close();
            pstmt.close();

            // 2. 없으면 생성
            String insertSql = "INSERT INTO Basket (basket_id, student_id) VALUES (?, ?)";
            pstmt = conn.prepareStatement(insertSql);
            pstmt.setString(1, basketId);
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
        String basketId = String.valueOf(studentId);
        String sql = "SELECT COUNT(*) " +
                "FROM BasketItem " +
                "WHERE basket_id = ? AND section_id = ?";

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
     * 장바구니에 분반 추가
     */
    public boolean addSectionToBasket(int studentId, String sectionId) throws SQLException {
        String basketId = String.valueOf(studentId);

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        boolean success = false;

        try {
            conn = DBConnection.getConnection();

            // 1. BasketItem에 INSERT
            String sql = "INSERT INTO BasketItem " +
                    "(registration_time, status, processed_time, reason, basket_id, section_id) " +
                    "VALUES (SYSDATE, 'PENDING', NULL, NULL, ?, ?)";

            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, basketId);
            pstmt.setString(2, sectionId);
            pstmt.executeUpdate();
            pstmt.close();

            // 2. 정원 확인 및 등록 처리
            success = processCapacityAndMaybeEnroll(conn, studentId, basketId, sectionId);

            DBConnection.commit(conn);

        } catch (SQLException e) {
            DBConnection.rollback(conn);
            throw e;
        } finally {
            DBConnection.close(rs, pstmt, conn);
        }

        return success;
    }

    /**
     * 정원 확인 및 등록 처리
     * private 메서드이므로 Connection을 파라미터로 받음
     */
    private boolean processCapacityAndMaybeEnroll(Connection conn, int studentId, String basketId, String sectionId) throws SQLException {
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            // 1) 분반 정원 가져오기
            int capacity = 0;
            String capSql = "SELECT capacity FROM Section WHERE section_id = ?";
            pstmt = conn.prepareStatement(capSql);
            pstmt.setString(1, sectionId);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                capacity = rs.getInt(1);
            } else {
                // 존재하지 않는 분반이면 FAILED 처리
                String upd = "UPDATE BasketItem " +
                        "SET status = 'FAILED', reason = '존재하지 않는 분반', processed_time = SYSDATE " +
                        "WHERE basket_id = ? AND section_id = ?";
                pstmt.close();
                rs.close();

                pstmt = conn.prepareStatement(upd);
                pstmt.setString(1, basketId);
                pstmt.setString(2, sectionId);
                pstmt.executeUpdate();
                pstmt.close();

                throw new SQLException("해당 SECTION_ID를 가진 분반이 없습니다: " + sectionId);
            }
            rs.close();
            pstmt.close();

            // 2) 현재 이 분반을 담고 있는 장바구니 인원 수 계산
            int count = 0;
            String cntSql = "SELECT COUNT(*) FROM BasketItem WHERE section_id = ?";
            pstmt = conn.prepareStatement(cntSql);
            pstmt.setString(1, sectionId);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                count = rs.getInt(1);
            }
            rs.close();
            pstmt.close();

            // 3) 정원 초과 여부 판단
            if (count > capacity) {
                // 정원 초과 -> FAILED 처리
                String upd = "UPDATE BasketItem " +
                        "SET status = 'FAILED', reason = '정원 초과', processed_time = SYSDATE " +
                        "WHERE basket_id = ? AND section_id = ?";
                pstmt = conn.prepareStatement(upd);
                pstmt.setString(1, basketId);
                pstmt.setString(2, sectionId);
                pstmt.executeUpdate();
                pstmt.close();

                return false;   // 정원 초과
            } else {
                // 정원 내 -> SUCCESS 처리 + ENROLLMENT에 INSERT
                String upd = "UPDATE BasketItem " +
                        "SET status = 'SUCCESS', reason = NULL, processed_time = SYSDATE " +
                        "WHERE basket_id = ? AND section_id = ?";
                pstmt = conn.prepareStatement(upd);
                pstmt.setString(1, basketId);
                pstmt.setString(2, sectionId);
                pstmt.executeUpdate();
                pstmt.close();

                // enrollment_id 생성
                String nextIdSql = "SELECT NVL(MAX( " +
                        "CASE " +
                        "WHEN REGEXP_LIKE(enrollment_id, '^[0-9]+$') " +
                        "THEN TO_NUMBER(enrollment_id) " +
                        "ELSE NULL " +
                        "END), 0) + 1 AS next_id " +
                        "FROM Enrollment";

                pstmt = conn.prepareStatement(nextIdSql);
                rs = pstmt.executeQuery();

                String nextEnrollmentId = "1";
                if (rs.next()) {
                    long nextId = rs.getLong(1);
                    nextEnrollmentId = String.valueOf(nextId);
                }
                rs.close();
                pstmt.close();

                // ENROLLMENT 테이블에 등록
                String enrSql = "INSERT INTO Enrollment " +
                        "(enrollment_id, enrollment_source, points_used, enrollment_time, student_id, section_id) " +
                        "VALUES (?, 'FROM_BASKET', 0, SYSDATE, ?, ?)";

                pstmt = conn.prepareStatement(enrSql);
                pstmt.setString(1, nextEnrollmentId);
                pstmt.setInt(2, studentId);
                pstmt.setString(3, sectionId);
                pstmt.executeUpdate();
                pstmt.close();

                return true;    // 성공적으로 등록까지 완료
            }

        } finally {
            if (rs != null) rs.close();
            if (pstmt != null) pstmt.close();
        }
    }

    /**
     * 로그인한 학생의 수강꾸러미 조회
     */
    public List<BasketItemDetail> getMyBasket(int studentId) throws SQLException {
        String sql = "SELECT s.section_id, s.section_number, s.professor, " +
                "s.capacity, s.classroom, s.course_id, c.course_name " +
                "FROM Basket b " +
                "JOIN BasketItem bi ON b.basket_id = bi.basket_id " +
                "JOIN Section s ON bi.section_id = s.section_id " +
                "JOIN Course c ON s.course_id = c.course_id " +
                "WHERE b.student_id = ? " +
                "ORDER BY s.section_id DESC";

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
     * 장바구니에서 분반 삭제 (basketId + sectionId 기준)
     */
    public int deleteSectionFromBasket(String basketId, String sectionId) throws SQLException {
        if (basketId == null) return 0;

        String sql = "DELETE FROM BasketItem WHERE basket_id = ? AND section_id = ?";

        Connection conn = null;
        PreparedStatement pstmt = null;
        int result = 0;

        try {
            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, basketId);
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
}