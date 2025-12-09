package com.team12.auction.dao;

import com.team12.auction.model.entity.Log;
import com.team12.auction.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LogDAO {
    /**
     * 해당 학생의 최근 limit개의 로그 얻기
     */
    public List<Log> getRecentLogsByStudent(int studentId, int limit) throws SQLException {
        String sql = "SELECT log_id, action_type, timestamp, details, auction_id " +
            "FROM Log " +
            "WHERE student_id = ? " +
            "ORDER BY timestamp DESC " +
            "FETCH FIRST ? ROWS ONLY";

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<Log> logs = new ArrayList<>();

        try {
            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement(sql);

            pstmt.setInt(1, studentId);
            pstmt.setInt(2, limit);

            rs = pstmt.executeQuery();

            while (rs.next()) {
                Log log = new Log();
                log.setLogId(rs.getString(1));
                log.setActionType(rs.getString(2));
                log.setTimestamp(rs.getTimestamp(3));
                log.setDetails(rs.getString(4));
                log.setStudentId(studentId);

                // auction_id can be null
                String auctionId = rs.getString(5);
                if (!rs.wasNull()) {
                    log.setAuctionId(auctionId);
                }

                logs.add(log);
            }
        } finally {
            DBConnection.close(rs, pstmt, conn);
        }

        return logs;
    }

    /**
     * 해당 학생의 로그 얻기
     */
    public List<Log> getAllLogsByStudent(int studentId) throws SQLException {
        String sql = "SELECT log_id, action_type, timestamp, details, auction_id " +
            "FROM Log " +
            "WHERE student_id = ? " +
            "ORDER BY timestamp DESC";

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<Log> logs = new ArrayList<>();

        try {
            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement(sql);

            pstmt.setInt(1, studentId);

            rs = pstmt.executeQuery();

            while (rs.next()) {
                Log log = new Log();
                log.setLogId(rs.getString(1));
                log.setActionType(rs.getString(2));
                log.setTimestamp(rs.getTimestamp(3));
                log.setDetails(rs.getString(4));
                log.setStudentId(studentId);

                String auctionId = rs.getString(5);
                if (!rs.wasNull()) {
                    log.setAuctionId(auctionId);
                }

                logs.add(log);
            }
        } finally {
            DBConnection.close(rs, pstmt, conn);
        }

        return logs;
    }

    /**
     * actionType의 로그 조회
     */
    public List<Log> getLogsByActionType(int studentId, String actionType) throws SQLException {
        String sql = "SELECT log_id, action_type, timestamp, details, auction_id " +
            "FROM Log " +
            "WHERE student_id = ? AND action_type = ? " +
            "ORDER BY timestamp DESC";

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<Log> logs = new ArrayList<>();

        try {
            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement(sql);

            pstmt.setInt(1, studentId);
            pstmt.setString(2, actionType);

            rs = pstmt.executeQuery();

            while (rs.next()) {
                Log log = new Log();
                log.setLogId(rs.getString(1));
                log.setActionType(rs.getString(2));
                log.setTimestamp(rs.getTimestamp(3));
                log.setDetails(rs.getString(4));
                log.setStudentId(studentId);

                String auctionId = rs.getString(5);
                if (!rs.wasNull()) {
                    log.setAuctionId(auctionId);
                }

            }
        } finally {
            DBConnection.close(rs, pstmt, conn);
        }

        return logs;
    }

    /**
     * 새로운 로그 삽입
     * <p>
     * log_id는 새로 만들어집니다.
     */
    public boolean insertLog(Log log) throws SQLException {
        String sql = "INSERT INTO Log (log_id, action_type, timestamp, details, " +
            "student_id, auction_id) " +
            "VALUES (?, ?, ?, ?, ?, ?)";

        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = DBConnection.getConnection();
            String logId = generateLogSequence(conn);
            pstmt = conn.prepareStatement(sql);

            pstmt.setString(1, logId);
            pstmt.setString(2, log.getActionType());
            pstmt.setTimestamp(3, log.getTimestamp());
            pstmt.setString(4, log.getDetails());
            pstmt.setInt(5, log.getStudentId());

            if (log.getAuctionId() != null) {
                pstmt.setString(6, log.getAuctionId());
            } else {
                pstmt.setNull(6, Types.VARCHAR);
            }

            int rowsAffected = pstmt.executeUpdate();
            log.setLogId(logId);
            return rowsAffected > 0;
        } catch (SQLException e) {
            DBConnection.rollback(conn);
            throw e;
        } finally {
            DBConnection.close(pstmt, conn);
        }
    }

    /**
     *
     */
    public int getLogCountByStudent(String studentId) throws SQLException {
        String sql = "SELECT COUNT(*) as log_count FROM Log WHERE student_id = ?";

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement(sql);

            pstmt.setString(1, studentId);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1);
            }
            return 0;
        } finally {
            DBConnection.close(rs, pstmt, conn);
        }
    }

    /**
     * LOG_ID 생성 (L0001, L0002, ...)
     *
     * @return Generated log ID (e.g., "L0001")
     */
    public String generateLogSequence(Connection conn) throws SQLException {
        String sql = "SELECT 'L' || LPAD(NVL(MAX(TO_NUMBER(SUBSTR(log_id, 2))), 0) + 1, 4, '0') FROM Log";

        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String sequence = "L0001";

        try {
            // FOR UPDATE로 락 걸기 (더미 테이블 사용)
            String lockSql = "SELECT 1 FROM Log WHERE ROWNUM = 1 FOR UPDATE";
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
}
