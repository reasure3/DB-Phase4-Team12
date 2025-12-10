package com.team12.auction.service;

import com.team12.auction.dao.AuctionDAO;
import com.team12.auction.dao.BidDAO;
import com.team12.auction.model.dto.AuctionDetail;
import com.team12.auction.util.DBConnection;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class AuctionService {

        private final AuctionDAO auctionDAO;
        private final BidDAO bidDAO;

        public AuctionService() {
                this.auctionDAO = new AuctionDAO();
                this.bidDAO = new BidDAO();
        }

        /**
         * 종료된 경매에 대해 상위 입찰자를 낙찰 처리하고 수강 신청을 자동 등록한다.
         */
        public void finalizeExpiredAuctions() throws SQLException {
                List<AuctionDetail> expiredAuctions = auctionDAO.selectExpiredActiveAuctions();

                for (AuctionDetail auction : expiredAuctions) {
                        Connection conn = null;
                        try {
                                conn = DBConnection.getConnection();
                                bidDAO.finalizeAuctionBids(conn, auction.getAuctionId(), auction.getAvailableSlots(), auction.getSectionId());
                                auctionDAO.updateStatus(conn, auction.getAuctionId(), "COMPLETED");
                                DBConnection.commit(conn);
                        } catch (SQLException e) {
                                DBConnection.rollback(conn);
                                throw e;
                        } finally {
                                DBConnection.close(null, conn);
                        }
                }
        }
}
