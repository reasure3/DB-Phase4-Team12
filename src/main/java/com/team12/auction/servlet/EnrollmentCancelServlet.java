package com.team12.auction.servlet;

import com.team12.auction.dao.EnrollmentDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/enrollment/cancel")
public class EnrollmentCancelServlet extends HttpServlet {

        private EnrollmentDAO enrollmentDAO;

        @Override
        public void init() throws ServletException {
                enrollmentDAO = new EnrollmentDAO();
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

                String sectionId = request.getParameter("sectionId");
                if (sectionId == null || sectionId.trim().isEmpty()) {
                        session.setAttribute("errorMessage", "잘못된 요청입니다. 다시 시도해주세요.");
                        response.sendRedirect(request.getContextPath() + "/enrollment/list");
                        return;
                }

                int studentId = (Integer) session.getAttribute("studentId");

                try {
                        int deleted = enrollmentDAO.deleteEnrollment(studentId, sectionId);
                        if (deleted > 0) {
                                session.setAttribute("successMessage", "수강 신청을 취소했습니다.");
                        } else {
                                session.setAttribute("errorMessage", "해당 수강 신청을 찾을 수 없습니다.");
                        }
                } catch (SQLException e) {
                        e.printStackTrace();
                        session.setAttribute("errorMessage", "수강 취소 처리 중 오류가 발생했습니다.");
                }

                response.sendRedirect(request.getContextPath() + "/enrollment/list");
        }
}
