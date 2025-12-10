package com.team12.auction.servlet;

import com.team12.auction.dao.EnrollmentDAO;
import com.team12.auction.model.dto.EnrollmentDetail;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MyEnrollmentServlet extends HttpServlet {
        private EnrollmentDAO enrollmentDAO;

        @Override
        public void init() throws ServletException {
                enrollmentDAO = new EnrollmentDAO();
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
                        List<EnrollmentDetail> enrollments = enrollmentDAO.getMyEnrollment(studentId);
                        request.setAttribute("enrollments", enrollments);
                } catch (SQLException e) {
                        e.printStackTrace();
                        request.setAttribute("enrollments", new ArrayList<>());
                        request.setAttribute("errorMessage", "수강신청 내역을 불러오는 중 오류가 발생했습니다.");
                }

                request.getRequestDispatcher("/enrollment/myEnrollment.jsp").forward(request, response);
        }
}
