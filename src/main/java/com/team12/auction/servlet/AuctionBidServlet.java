package com.team12.auction.servlet;

import com.team12.auction.dao.AuctionDAO;
import com.team12.auction.dao.BasketDAO;
import com.team12.auction.dao.BidDAO;
import com.team12.auction.model.dto.AuctionDetail;
import com.team12.auction.model.dto.BidDetail;
import com.team12.auction.model.entity.Bid;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/auction/bid")
public class AuctionBidServlet extends HttpServlet {

        private AuctionDAO auctionDAO;
        private BidDAO bidDAO;
        private BasketDAO basketDAO;

        @Override
        public void init() throws ServletException {
                auctionDAO = new AuctionDAO();
                bidDAO = new BidDAO();
                basketDAO = new BasketDAO();
        }

        @Override
        protected void doGet(HttpServletRequest request, HttpServletResponse response)
                        throws ServletException, IOException {
                request.setCharacterEncoding("UTF-8");

                HttpSession session = request.getSession(false);
                if (session == null || session.getAttribute("studentId") == null) {
                        response.sendRedirect(request.getContextPath() + "/auth/login");
                        return;
                }

                int studentId = (Integer) session.getAttribute("studentId");
                String auctionId = request.getParameter("auctionId");

                renderBidPage(request, response, studentId, auctionId);
        }

        @Override
        protected void doPost(HttpServletRequest request, HttpServletResponse response)
                        throws ServletException, IOException {
                request.setCharacterEncoding("UTF-8");

                HttpSession session = request.getSession(false);
                if (session == null || session.getAttribute("studentId") == null) {
                        response.sendRedirect(request.getContextPath() + "/auth/login");
                        return;
                }

                int studentId = (Integer) session.getAttribute("studentId");
                String auctionId = request.getParameter("auctionId");
                String bidAmountStr = request.getParameter("bidAmount");

                if (auctionId == null || auctionId.trim().isEmpty()) {
                        request.setAttribute("errorMessage", "경매 ID가 존재하지 않습니다.");
                        renderBidPage(request, response, studentId, auctionId);
                        return;
                }

                int bidAmount;
                try {
                        bidAmount = Integer.parseInt(bidAmountStr);
                } catch (NumberFormatException e) {
                        request.setAttribute("errorMessage", "입찰 금액이 올바르지 않습니다.");
                        renderBidPage(request, response, studentId, auctionId);
                        return;
                }

                if (bidAmount <= 0) {
                        request.setAttribute("errorMessage", "입찰 금액은 1 이상이어야 합니다.");
                        renderBidPage(request, response, studentId, auctionId);
                        return;
                }

                try {
                        AuctionDetail auction = auctionDAO.selectById(auctionId);
                        if (auction == null) {
                                request.setAttribute("errorMessage", "존재하지 않는 경매입니다.");
                                renderBidPage(request, response, studentId, auctionId);
                                return;
                        }

                        boolean inBasket = basketDAO.isSectionInBasket(studentId, auction.getSectionId());
                        if (!inBasket) {
                                request.setAttribute("errorMessage", "수강꾸러미에 담지 않은 분반의 경매는 입찰할 수 없습니다.");
                                renderBidPage(request, response, studentId, auctionId);
                                return;
                        }

                        boolean biddingWindowOpen = isWithinBidWindow(auction);
                        if (!biddingWindowOpen || !"ACTIVE".equalsIgnoreCase(auction.getStatus())) {
                                request.setAttribute("errorMessage", "입찰 가능 기간이 아니거나 비활성화된 경매입니다.");
                                renderBidPage(request, response, studentId, auctionId);
                                return;
                        }

                        if (bidDAO.hasAlreadyBid(auctionId, studentId)) {
                                request.setAttribute("errorMessage", "이미 입찰한 경매입니다.");
                                renderBidPage(request, response, studentId, auctionId);
                                return;
                        }

                        int prospectiveRank = bidDAO.calculateProspectiveRank(auctionId, bidAmount);
                        if (prospectiveRank > auction.getAvailableSlots()) {
                                request.setAttribute("errorMessage", "입찰 금액이 수강 가능 인원 범위 안에 들지 않아 입찰할 수 없습니다.");
                                renderBidPage(request, response, studentId, auctionId);
                                return;
                        }

                        Bid bid = new Bid();
                        // bid_sequence는 insertBid 내부에서 자동 생성됨
//                        bid.setBidSequence(bidDAO.generateBidSequence());
                        bid.setBidAmount(bidAmount);
                        bid.setAuctionId(auctionId);
                        bid.setStudentId(studentId);

                        bidDAO.insertBid(bid);
                        request.setAttribute("successMessage", "입찰이 완료되었습니다. 현재 예상 순위: " + prospectiveRank + "위");
                } catch (SQLException e) {
                        e.printStackTrace();
                        request.setAttribute("errorMessage", "입찰 처리 중 오류가 발생했습니다.");
                }

                renderBidPage(request, response, studentId, auctionId);
        }

        private void renderBidPage(HttpServletRequest request, HttpServletResponse response, int studentId, String auctionId)
                        throws ServletException, IOException {

                if (auctionId == null || auctionId.trim().isEmpty()) {
                        request.setAttribute("errorMessage", "경매 ID가 존재하지 않습니다.");
                        request.getRequestDispatcher("/auction/bidForm.jsp").forward(request, response);
                        return;
                }

                try {
                        AuctionDetail auction = auctionDAO.selectById(auctionId);
                        if (auction == null) {
                                request.setAttribute("errorMessage", "존재하지 않는 경매입니다.");
                                request.getRequestDispatcher("/auction/bidForm.jsp").forward(request, response);
                                return;
                        }

                        boolean inBasket = basketDAO.isSectionInBasket(studentId, auction.getSectionId());
                        List<BidDetail> bids = bidDAO.selectByAuctionId(auctionId);

                        List<BidDetail> topBids = new ArrayList<>();
                        int availableSlots = auction.getAvailableSlots();
                        for (BidDetail bid : bids) {
                                if (topBids.size() >= availableSlots) {
                                        break;
                                }
                                topBids.add(bid);
                        }

                        boolean biddingWindowOpen = isWithinBidWindow(auction);
                        boolean alreadyBid = bidDAO.hasAlreadyBid(auctionId, studentId);
                        boolean canBid = inBasket && biddingWindowOpen && "ACTIVE".equalsIgnoreCase(auction.getStatus()) && !alreadyBid;

                        request.setAttribute("auction", auction);
                        request.setAttribute("topBids", topBids);
                        request.setAttribute("biddingWindowOpen", biddingWindowOpen);
                        request.setAttribute("alreadyBid", alreadyBid);
                        request.setAttribute("inBasket", inBasket);
                        request.setAttribute("canBid", canBid);
                        request.setAttribute("totalBidCount", bids.size());
                } catch (SQLException e) {
                        e.printStackTrace();
                        request.setAttribute("errorMessage", "입찰 정보를 불러오는 중 오류가 발생했습니다.");
                }

                request.getRequestDispatcher("/auction/bidForm.jsp").forward(request, response);
        }

        private boolean isWithinBidWindow(AuctionDetail auction) {
                if (auction.getStartTime() == null || auction.getEndTime() == null) {
                        return false;
                }

                LocalDate today = LocalDate.now();
                LocalDate start = auction.getStartTime().toLocalDate();
                LocalDate end = auction.getEndTime().toLocalDate();

                return !today.isBefore(start) && !today.isAfter(end);
        }
}
