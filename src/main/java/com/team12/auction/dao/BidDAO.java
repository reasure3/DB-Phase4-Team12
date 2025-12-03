package com.team12.auction.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.team12.auction.model.dto.BidDetail;
import com.team12.auction.model.entity.Bid;

public class BidDAO extends BaseDao {

	public BidDAO(Connection conn) {
		super(conn);
	}
	
	/**
     * 특정 경매의 입찰 목록 조회 (포인트 높은순, 시간 빠른순)
     */
    public List<BidDetail> selectByAuctionId(String auctionId) throws SQLException {
        String sql = 
            "SELECT b.bid_sequence, b.bid_amount, b.bid_time, b.is_successful, " +
            "       b.auction_id, b.student_id, s.name " +
            "FROM BID b " +
            "JOIN STUDENT s ON b.student_id = s.student_id " +
            "WHERE b.auction_id = ? " +
            "ORDER BY b.bid_amount DESC, b.bid_time ASC";
        
        List<BidDetail> bids = new ArrayList<>();
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, auctionId);
            ResultSet rs = pstmt.executeQuery();
            
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
            rs.close();
        }
        
        return bids;
    }
    
    /**
     * 입찰하기 + 즉시 낙찰 판단
     * @return 낙찰 여부 (true: 낙찰, false: 탈락)
     */
    public boolean insertAndCheckWinner(Bid bid, int availableSlots, String sectionId) throws SQLException {
        try {
        	// 1. 입찰 INSERT (bid_time은 SYSDATE로 자동 설정)
            String insertBidSql = 
                "INSERT INTO BID (bid_sequence, bid_amount, bid_time, auction_id, student_id) " +
                "VALUES (?, ?, SYSDATE, ?, ?)";
            
            try (PreparedStatement pstmt = conn.prepareStatement(insertBidSql)) {
                pstmt.setString(1, bid.getBidSequence());
                pstmt.setInt(2, bid.getBidAmount());
                pstmt.setString(3, bid.getAuctionId());
                pstmt.setInt(4, bid.getStudentId());
                pstmt.executeUpdate();
            }
            
            // 2. 내 순위 계산
            String rankSql = 
                "SELECT COUNT(*) + 1 AS my_rank " +
                "FROM BID " +
                "WHERE auction_id = ? " +
                "  AND (bid_amount > ? OR (bid_amount = ? AND bid_time < " +
                "      (SELECT bid_time FROM BID WHERE bid_sequence = ?)))";
            
            int myRank = 0;
            try (PreparedStatement pstmt = conn.prepareStatement(rankSql)) {
                pstmt.setString(1, bid.getAuctionId());
                pstmt.setInt(2, bid.getBidAmount());
                pstmt.setInt(3, bid.getBidAmount());
                pstmt.setString(4, bid.getBidSequence());
                ResultSet rs = pstmt.executeQuery();
                
                if (rs.next()) {
                    myRank = rs.getInt(1);
                }
                rs.close();
            }
            
            // 3. 낙찰 여부 판단
            boolean isWinner = myRank <= availableSlots;
            
            if (isWinner) {
                // 4-1. BID 테이블 업데이트
                String updateBidSql = "UPDATE BID SET is_successful = 'Y' WHERE bid_sequence = ?";
                try (PreparedStatement pstmt = conn.prepareStatement(updateBidSql)) {
                    pstmt.setString(1, bid.getBidSequence());
                    pstmt.executeUpdate();
                }
                
                // 4-2. ENROLLMENT 테이블에 INSERT (enrollment_time은 SYSDATE로 자동 설정)
                String enrollmentId = generateEnrollmentId();
                String insertEnrollmentSql = 
                    "INSERT INTO ENROLLMENT (enrollment_id, enrollment_source, points_used, enrollment_time, student_id, section_id) " +
                    "VALUES (?, 'FROM_AUCTION', ?, SYSDATE, ?, ?)";
                
                try (PreparedStatement pstmt = conn.prepareStatement(insertEnrollmentSql)) {
                    pstmt.setString(1, enrollmentId);
                    pstmt.setInt(2, bid.getBidAmount());
                    pstmt.setInt(3, bid.getStudentId());
                    pstmt.setString(4, sectionId);
                    pstmt.executeUpdate();
                }
            }
            
            return isWinner;
            
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
    }
    
    /**
     * 학생이 특정 경매에 이미 입찰했는지 확인
     */
    public boolean hasAlreadyBid(String auctionId, int studentId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM BID WHERE auction_id = ? AND student_id = ?";
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, auctionId);
            pstmt.setInt(2, studentId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
            return false;
        }
    }

    /**
     * 학생의 특정 경매 입찰이 낙찰되었는지 확인
     */
    public boolean isSuccessfulBid(String auctionId, int studentId) throws SQLException {
        String sql = "SELECT is_successful FROM BID WHERE auction_id = ? AND student_id = ?";
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, auctionId);
            pstmt.setInt(2, studentId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getString(1).equals("Y");
            }
            return false;
        }
    }
    
    /**
     * 특정 경매의 총 입찰자 수 조회
     */
    public int countBidsByAuctionId(String auctionId) throws SQLException {
        String sql = "SELECT COUNT(DISTINCT student_id) FROM BID WHERE auction_id = ?";
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, auctionId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1);
            }
            return 0;
        }
    }
    
    /**
     * BID_SEQUENCE 생성 (BID001, BID002, ...)
     */
    public String generateBidSequence() throws SQLException {
        String sql = "SELECT 'BID' || LPAD(NVL(MAX(TO_NUMBER(SUBSTR(bid_sequence, 4))), 0) + 1, 3, '0') FROM BID";
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) {
                return rs.getString(1);
            }
            return "BID001";
        }
    }
    
    /**
     * 학생의 총 입찰 포인트 계산
     */
    public int getTotalBidPoints(int studentId) throws SQLException {
        String sql = "SELECT NVL(SUM(bid_amount), 0) FROM BID WHERE student_id = ?";
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, studentId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1);
            }
            return 0;
        }
    }
    
    /**
     * ENROLLMENT_ID 생성 (E0001, E0002, ...)
     */
    private String generateEnrollmentId() throws SQLException {
        String sql = "SELECT 'E' || LPAD(NVL(MAX(TO_NUMBER(SUBSTR(enrollment_id, 2))), 0) + 1, 4, '0') FROM ENROLLMENT";
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) {
                return rs.getString(1);
            }
            return "E0001";
        }
    }
}
