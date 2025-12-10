package com.team12.auction.servlet;

import com.team12.auction.dao.AuctionDAO;
import com.team12.auction.model.dto.AuctionDetail;
import com.team12.auction.model.entity.Bid;
import com.team12.auction.service.AuctionService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;

@WebServlet("/auction/list")
public class AuctionListServlet extends HttpServlet {

        private AuctionDAO auctionDAO;
        private AuctionService auctionService;

        @Override
        public void init() throws ServletException {
                auctionDAO = new AuctionDAO();
                auctionService = new AuctionService();
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

                try {
                        auctionService.finalizeExpiredAuctions();
                        Map<AuctionDetail, Bid> auctionBidMap = auctionDAO.selectMyAuctions(studentId);

                        // LinkedHashMap으로 복사해 조회 순서를 유지
                        Map<AuctionDetail, Bid> orderedMap = new LinkedHashMap<>(auctionBidMap);
                        request.setAttribute("auctionBidMap", orderedMap);
                } catch (SQLException e) {
                        e.printStackTrace();
                        request.setAttribute("auctionBidMap", new LinkedHashMap<AuctionDetail, Bid>());
                        request.setAttribute("errorMessage", "경매 정보를 불러오는 중 오류가 발생했습니다.");
                }

                request.getRequestDispatcher("/auction/auctionList.jsp").forward(request, response);
        }
}
