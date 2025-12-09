package com.team12.auction.servlet;

import com.team12.auction.dao.BasketDAO;
import com.team12.auction.dao.StudentDAO;
import com.team12.auction.model.dto.BasketItemDetail;
import com.team12.auction.model.entity.Student;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/basket/list")
public class BasketListServlet extends HttpServlet {

    private BasketDAO basketDAO;
    private StudentDAO studentDAO;

    @Override
    public void init() throws ServletException {
        basketDAO = new BasketDAO();
        studentDAO = new StudentDAO();
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

        // 세션에 있던 메시지 옮기기 (한 번만 보여주고 끝)
        String successMessage = (String) session.getAttribute("successMessage");
        String errorMessage = (String) session.getAttribute("errorMessage");
        if (successMessage != null) {
            request.setAttribute("successMessage", successMessage);
            session.removeAttribute("successMessage");
        }
        if (errorMessage != null) {
            request.setAttribute("errorMessage", errorMessage);
            session.removeAttribute("errorMessage");
        }

        try {
            // 1) 장바구니 보장 + 조회
            basketDAO.ensureBasketExists(studentId);
            List<BasketItemDetail> basketItems = basketDAO.getMyBasket(studentId);

            // 2) 총 학점 계산
            int totalCredits = 0;
            for (BasketItemDetail item : basketItems) {
                totalCredits += item.getCredits();
            }

            // 3) 학생 최대 학점 조회
            Student stu = studentDAO.selectById(studentId);
            int maxCredits = (stu != null) ? stu.getMaxCredits() : 0;  // 메서드 이름은 실제 엔티티에 맞춰 수정

            // 4) JSP에 넘기기
            request.setAttribute("basketItems", basketItems);
            request.setAttribute("totalCredits", totalCredits);
            request.setAttribute("maxCredits", maxCredits);

            request.getRequestDispatcher("/basket/BasketList.jsp")
                   .forward(request, response);

        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "수강꾸러미 정보를 불러오는 중 오류가 발생했습니다.");
            request.getRequestDispatcher("/basket/BasketList.jsp")
                   .forward(request, response);
        }
    }
}
