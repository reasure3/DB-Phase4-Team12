package com.team12.auction.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.team12.auction.model.dto.AuctionDetail;
import com.team12.auction.model.entity.Auction;
import com.team12.auction.model.entity.Bid;

public class AuctionDAO extends BaseDao {

	public AuctionDAO(Connection conn) {
		super(conn);
	}

	/**
     * 학과별 경매 조회 (ACTIVE 또는 COMPLETED 상태)
     */
    public List<AuctionDetail> selectByDepartment(String department) throws SQLException {
        String sql = 
            "SELECT a.auction_id, a.start_time, a.end_time, a.status, a.available_slots, " +
            "       a.created_at, a.section_id, " +
            "       s.section_number, s.professor, " +
            "       c.course_id, c.course_name, c.department, c.credits " +
            "FROM AUCTION a " +
            "JOIN SECTION s ON a.section_id = s.section_id " +
            "JOIN COURSE c ON s.course_id = c.course_id " +
            "WHERE c.department = ? " +
            "  AND a.status IN ('ACTIVE', 'COMPLETED') " +
            "ORDER BY a.start_time DESC";
        
        List<AuctionDetail> auctions = new ArrayList<>();
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, department);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
            	AuctionDetail auction = new AuctionDetail();
                auction.setAuctionId(rs.getString(1));
                auction.setStartTime(rs.getDate(2));
                auction.setEndTime(rs.getDate(3));
                auction.setStatus(rs.getString(4));
                auction.setAvailableSlots(rs.getInt(5));
                auction.setCreatedAt(rs.getDate(6));
                auction.setSectionId(rs.getString(7));
                auction.setSectionNumber(rs.getInt(8));
                auction.setProfessor(rs.getString(9));
                auction.setCourseId(rs.getString(10));
                auction.setCourseName(rs.getString(11));
                auction.setDepartment(rs.getString(12));
                auction.setCredits(rs.getInt(13));
                auctions.add(auction);
            }
            rs.close();
        }
        
        return auctions;
    }
    
    /**
     * 나의 경매 조회 (참여 가능한 모든 경매 + 내 입찰 정보)
     * 입찰했으면 입찰 금액 표시, 안 했으면 0 표시
     */
    public Map<AuctionDetail, Bid> selectMyAuctions(int studentId) throws SQLException {
        String sql = 
            "SELECT a.auction_id, a.start_time, a.end_time, a.status, a.available_slots, " +
            "       a.created_at, a.section_id, " +
            "       s.section_number, s.professor, " +
            "       c.course_id, c.course_name, c.department, c.credits, " +
            "       b.bid_amount, b.is_successful " +
            "FROM AUCTION a " +
            "JOIN SECTION s ON a.section_id = s.section_id " +
            "JOIN COURSE c ON s.course_id = c.course_id " +
            "LEFT JOIN BID b ON b.auction_id = a.auction_id AND b.student_id = ? " +  // ✅ LEFT JOIN
            "WHERE a.status = 'COMPLETED' " +
            "ORDER BY a.start_time DESC";
        
        Map<AuctionDetail, Bid> auctionBidMap = new HashMap<>();
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, studentId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                // Auction 객체 생성
            	AuctionDetail auction = new AuctionDetail();
                auction.setAuctionId(rs.getString(1));
                auction.setStartTime(rs.getDate(2));
                auction.setEndTime(rs.getDate(3));
                auction.setStatus(rs.getString(4));
                auction.setAvailableSlots(rs.getInt(5));
                auction.setCreatedAt(rs.getDate(6));
                auction.setSectionId(rs.getString(7));
                auction.setSectionNumber(rs.getInt(8));
                auction.setProfessor(rs.getString(9));
                auction.setCourseId(rs.getString(10));
                auction.setCourseName(rs.getString(11));
                auction.setDepartment(rs.getString(12));
                auction.setCredits(rs.getInt(13));
                
                // Bid 객체 생성 (입찰 안 했으면 null 처리)
                Bid myBid = new Bid();
                if (rs.getObject(14) != null) {  // bid_amount가 null이 아니면
                    myBid.setBidAmount(rs.getInt(14)); 
                    myBid.setIsSuccessful(rs.getString(15));
                } else {
                    myBid.setBidAmount(0);                       // 입찰 안 함
                    myBid.setIsSuccessful("N");
                }
                
                auctionBidMap.put(auction, myBid);
            }
            rs.close();
        }
        
        return auctionBidMap;
    }
    
    /**
     * 경매 ID로 조회
     */
    public AuctionDetail selectById(String auctionId) throws SQLException {
        String sql = 
            "SELECT a.auction_id, a.start_time, a.end_time, a.status, a.available_slots, " +
            "       a.created_at, a.section_id, " +
            "       s.section_number, s.professor, s.capacity, " +
            "       c.course_id, c.course_name, c.department, c.credits " +
            "FROM AUCTION a " +
            "JOIN SECTION s ON a.section_id = s.section_id " +
            "JOIN COURSE c ON s.course_id = c.course_id " +
            "WHERE a.auction_id = ?";
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, auctionId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                AuctionDetail auction = new AuctionDetail();
                auction.setAuctionId(rs.getString(1));
                auction.setStartTime(rs.getDate(2));
                auction.setEndTime(rs.getDate(3));
                auction.setStatus(rs.getString(4));
                auction.setAvailableSlots(rs.getInt(5));
                auction.setCreatedAt(rs.getDate(6));
                auction.setSectionId(rs.getString(7));
                auction.setSectionNumber(rs.getInt(8));
                auction.setProfessor(rs.getString(9));
                auction.setCourseId(rs.getString(11));
                auction.setCourseName(rs.getString(12));
                auction.setDepartment(rs.getString(13));
                auction.setCredits(rs.getInt(14));
                rs.close();
                return auction;
            }
            rs.close();
            return null;
        }
    }
    
    /**
     * 경매 존재 여부 확인
     */
    public boolean existsById(String auctionId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM AUCTION WHERE auction_id = ?";
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, auctionId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
            return false;
        }
    }
}
