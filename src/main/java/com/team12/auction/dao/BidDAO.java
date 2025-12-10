package com.team12.auction.dao;

import com.team12.auction.model.dto.BidDetail;
import com.team12.auction.model.entity.Bid;
import com.team12.auction.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class BidDAO {
    /**
     * 특정 경매의 입찰 목록 조회 (포인트 높은순, 시간 빠른순)
     */
    public List<BidDetail> selectByAuctionId(String auctionId) throws SQLException {
        String sql = "SELECT b.bid_sequence, b.bid_amount, b.bid_time, b.is_successful, "
            + "b.auction_id, b.student_id, s.name " + "FROM Bid b "
            + "JOIN Student s ON b.student_id = s.student_id " + "WHERE b.auction_id = ? "
            + "ORDER BY b.bid_amount DESC, b.bid_time ASC";

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<BidDetail> bids = new ArrayList<>();

        try {
            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, auctionId);

            rs = pstmt.executeQuery();

            while (rs.next()) {
                BidDetail bid = new BidDetail();
                bid.setBidSequence(rs.getString(1));
                bid.setBidAmount(rs.getInt(2));
                bid.setBidTime(rs.getDate(3));
                bid.setIsSuccessful(rs.getString(4));
                bid.setAuctionId(rs.getString(5));
                bid.setStudentId(rs.getInt(6));
                bid.setStudentName(rs.getString(7));
                bids.add(bid);
            }

        } catch (SQLException e) {
            throw e;
        } finally {
            DBConnection.close(rs, pstmt, conn);
        }

        return bids;
    }

    /**
     * 입찰하기 + 즉시 낙찰 판단
     *
     * @return 낙찰 여부 (true: 낙찰, false: 탈락)
     */
    public boolean insertAndCheckWinner(Bid bid, int availableSlots, String sectionId) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        boolean isWinner = false;

        try {
            conn = DBConnection.getConnection();

            // 0. BID_SEQUENCE 생성 (락 적용됨)
            String bidSequence = generateBidSequence(conn);
            bid.setBidSequence(bidSequence);

            // 1. 입찰 INSERT
            String insertBidSql = "INSERT INTO Bid (bid_sequence, bid_amount, bid_time, auction_id, student_id) "
                + "VALUES (?, ?, SYSDATE, ?, ?)";

            pstmt = conn.prepareStatement(insertBidSql);
            pstmt.setString(1, bid.getBidSequence());
            pstmt.setInt(2, bid.getBidAmount());
            pstmt.setString(3, bid.getAuctionId());
            pstmt.setInt(4, bid.getStudentId());
            pstmt.executeUpdate();
            pstmt.close();

            // 2. 내 순위 계산
            String rankSql = "SELECT COUNT(*) + 1 AS my_rank " + "FROM Bid " + "WHERE auction_id = ? "
                + "AND (bid_amount > ? OR (bid_amount = ? AND bid_time < "
                + "(SELECT bid_time FROM Bid WHERE bid_sequence = ?)))";

            pstmt = conn.prepareStatement(rankSql);
            pstmt.setString(1, bid.getAuctionId());
            pstmt.setInt(2, bid.getBidAmount());
            pstmt.setInt(3, bid.getBidAmount());
            pstmt.setString(4, bid.getBidSequence());

            rs = pstmt.executeQuery();

            int myRank = 0;
            if (rs.next()) {
                myRank = rs.getInt(1);
            }
            rs.close();
            pstmt.close();

            // 3. 낙찰 여부 판단
            isWinner = myRank <= availableSlots;

            if (isWinner) {
                // 4-1. Bid 테이블 업데이트
                String updateBidSql = "UPDATE Bid SET is_successful = 'Y' WHERE bid_sequence = ?";
                pstmt = conn.prepareStatement(updateBidSql);
                pstmt.setString(1, bid.getBidSequence());
                pstmt.executeUpdate();
                pstmt.close();

                // 4-2. Enrollment 테이블에 INSERT (락 적용됨)
                String enrollmentId = generateEnrollmentId(conn);
                String insertEnrollmentSql = "INSERT INTO Enrollment (enrollment_id, enrollment_source, points_used, enrollment_time, student_id, section_id) "
                    + "VALUES (?, 'FROM_AUCTION', ?, SYSDATE, ?, ?)";

                pstmt = conn.prepareStatement(insertEnrollmentSql);
                pstmt.setString(1, enrollmentId);
                pstmt.setInt(2, bid.getBidAmount());
                pstmt.setInt(3, bid.getStudentId());
                pstmt.setString(4, sectionId);
                pstmt.executeUpdate();
                pstmt.close();
            }

            DBConnection.commit(conn);

        } catch (SQLException e) {
            DBConnection.rollback(conn);
            throw e;
        } finally {
            DBConnection.close(rs, pstmt, conn);
        }

        return isWinner;
    }

    /**
     * 새 입찰을 추가하기 전에 예상 순위를 계산한다 (동점일 경우 기존 입찰이 우선이므로 동점 건수를 모두 앞에 둔다).
     */
    public int calculateProspectiveRank(String auctionId, int bidAmount) throws SQLException {
        String sql = "SELECT COUNT(*) + 1 AS my_rank " + "FROM Bid " + "WHERE auction_id = ? "
            + "AND (bid_amount > ? OR bid_amount = ?)";

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        int rank = Integer.MAX_VALUE;

        try {
            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, auctionId);
            pstmt.setInt(2, bidAmount);
            pstmt.setInt(3, bidAmount);

            rs = pstmt.executeQuery();

            if (rs.next()) {
                rank = rs.getInt(1);
            }

        } catch (SQLException e) {
            throw e;
        } finally {
            DBConnection.close(rs, pstmt, conn);
        }

        return rank;
    }

    /**
     * 낙찰 여부와 관계없이 단순히 입찰을 기록한다.
     */
    public void insertBid(Bid bid) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = DBConnection.getConnection();

            // bid_sequence 생성 (락 적용됨)
            String bidSequence = generateBidSequence(conn);
            bid.setBidSequence(bidSequence);

            String insertBidSql = "INSERT INTO Bid (bid_sequence, bid_amount, bid_time, is_successful, auction_id, student_id) "
                + "VALUES (?, ?, SYSDATE, NULL, ?, ?)";

            pstmt = conn.prepareStatement(insertBidSql);
            pstmt.setString(1, bidSequence);
            pstmt.setInt(2, bid.getBidAmount());
            pstmt.setString(3, bid.getAuctionId());
            pstmt.setInt(4, bid.getStudentId());
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
     * 학생이 특정 경매에 이미 입찰했는지 확인
     */
    public boolean hasAlreadyBid(String auctionId, int studentId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM Bid WHERE auction_id = ? AND student_id = ?";

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        boolean hasBid = false;

        try {
            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, auctionId);
            pstmt.setInt(2, studentId);

            rs = pstmt.executeQuery();

            if (rs.next()) {
                hasBid = rs.getInt(1) > 0;
            }

        } catch (SQLException e) {
            throw e;
        } finally {
            DBConnection.close(rs, pstmt, conn);
        }

        return hasBid;
    }

    /**
     * 학생의 특정 경매 입찰이 낙찰되었는지 확인
     */
    public boolean isSuccessfulBid(String auctionId, int studentId) throws SQLException {
        String sql = "SELECT is_successful FROM Bid WHERE auction_id = ? AND student_id = ?";

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        boolean isSuccessful = false;

        try {
            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, auctionId);
            pstmt.setInt(2, studentId);

            rs = pstmt.executeQuery();

            if (rs.next()) {
                isSuccessful = "Y".equals(rs.getString(1));
            }

        } catch (SQLException e) {
            throw e;
        } finally {
            DBConnection.close(rs, pstmt, conn);
        }

        return isSuccessful;
    }

    /**
     * 특정 경매의 총 입찰자 수 조회
     */
    public int countBidsByAuctionId(String auctionId) throws SQLException {
        String sql = "SELECT COUNT(DISTINCT student_id) FROM Bid WHERE auction_id = ?";

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        int count = 0;

        try {
            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, auctionId);

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
     * BID_SEQUENCE 생성 (BID001, BID002, ...)
     */
    public String generateBidSequence(Connection conn) throws SQLException {
        String sql = "SELECT 'BID' || LPAD(NVL(MAX(TO_NUMBER(SUBSTR(bid_sequence, 4))), 0) + 1, 3, '0') FROM Bid";

        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String sequence = "BID001";

        try {
            // FOR UPDATE로 락 걸기
            String lockSql = "SELECT 1 FROM Bid WHERE ROWNUM = 1 FOR UPDATE";
            pstmt = conn.prepareStatement(lockSql);
            pstmt.executeQuery();
            pstmt.close();

            // 시퀀스 생성
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                sequence = rs.getString(1);
            }

            pstmt.close();
            rs.close();
        } catch (SQLException e) {
            DBConnection.rollback(conn);
            throw e;
        }

        return sequence;
    }

    /**
     * 학생의 총 입찰 포인트 계산
     */
    public int getTotalBidPoints(int studentId) throws SQLException {
        String sql = "SELECT NVL(SUM(bid_amount), 0) FROM Bid WHERE student_id = ?";

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        int total = 0;

        try {
            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, studentId);

            rs = pstmt.executeQuery();

            if (rs.next()) {
                total = rs.getInt(1);
            }

        } catch (SQLException e) {
            throw e;
        } finally {
            DBConnection.close(rs, pstmt, conn);
        }

        return total;
    }

    /**
     * ENROLLMENT_ID 생성 (E0001, E0002, ...) private 메서드이므로 Connection을 파라미터로 받음
     */
    private String generateEnrollmentId(Connection conn) throws SQLException {
        String sql = "SELECT 'E' || LPAD(NVL(MAX(TO_NUMBER(SUBSTR(enrollment_id, 2))), 0) + 1, 4, '0') FROM Enrollment";

        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String enrollmentId = "E0001";

        try {
            // FOR UPDATE로 락 걸기
            String lockSql = "SELECT 1 FROM Enrollment WHERE ROWNUM = 1 FOR UPDATE";
            pstmt = conn.prepareStatement(lockSql);
            pstmt.executeQuery();
            pstmt.close();

            // 시퀀스 생성
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                enrollmentId = rs.getString(1);
            }

            pstmt.close();
            rs.close();
        } catch (SQLException e) {
            DBConnection.rollback(conn);
            throw e;
        }

        return enrollmentId;
    }
}
