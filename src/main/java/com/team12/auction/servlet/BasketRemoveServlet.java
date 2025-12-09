package com.team12.auction.servlet;

import com.team12.auction.dao.BasketDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/basket/remove")
public class BasketRemoveServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("studentId") == null) {
            response.sendRedirect(request.getContextPath() + "/auth/login.jsp");
            return;
        }

        int studentId = (Integer) session.getAttribute("studentId");
        String sectionId = request.getParameter("sectionId");
        String returnUrl = request.getParameter("returnUrl");
        if (returnUrl == null || returnUrl.isBlank()) {
            returnUrl = request.getContextPath() + "/basket/myBasket.jsp";
        }

        if (sectionId == null || sectionId.isBlank()) {
            session.setAttribute("errorMessage", "삭제할 분반 정보가 없습니다.");
            response.sendRedirect(returnUrl);
            return;
        }

        BasketDAO basketDAO = new BasketDAO();

        try {
            String basketId = basketDAO.getBasketId(studentId);
            int deleted = basketDAO.deleteSectionFromBasket(basketId, sectionId);
            if (deleted > 0) {
                session.setAttribute("successMessage", "수강꾸러미에서 분반을 삭제했습니다.");
            } else {
                session.setAttribute("errorMessage", "장바구니에 존재하지 않는 분반입니다.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            session.setAttribute("errorMessage", "삭제 중 오류가 발생했습니다: " + e.getMessage());
        }

        response.sendRedirect(returnUrl);
    }
}
