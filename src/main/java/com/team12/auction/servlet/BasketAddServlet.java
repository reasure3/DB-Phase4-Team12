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

@WebServlet("/basket/add")
public class BasketAddServlet extends HttpServlet {
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
            returnUrl = request.getContextPath() + "/section/sectionList.jsp";
        }

        if (sectionId == null || sectionId.isBlank()) {
            session.setAttribute("errorMessage", "담을 분반 정보가 없습니다.");
            response.sendRedirect(returnUrl);
            return;
        }

        BasketDAO basketDAO = new BasketDAO();

        try {
            basketDAO.ensureBasketExists(studentId);

            if (basketDAO.isSectionInBasket(studentId, sectionId)) {
                session.setAttribute("errorMessage", "이미 수강꾸러미에 담은 분반입니다.");
            } else {
                boolean enrolled = basketDAO.addSectionToBasket(studentId, sectionId);
                if (enrolled) {
                    session.setAttribute("successMessage", "분반을 수강꾸러미에 담고 등록까지 완료했습니다.");
                } else {
                    session.setAttribute("successMessage", "분반을 수강꾸러미에 담았습니다. 등록은 정원 확인 후 처리됩니다.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            session.setAttribute("errorMessage", "장바구니 담기에 실패했습니다: " + e.getMessage());
        }

        response.sendRedirect(returnUrl);
    }
}
