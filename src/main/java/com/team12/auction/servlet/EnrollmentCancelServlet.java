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
                String sectionId = request.getParameter("sectionId");
                String returnUrl = request.getParameter("returnUrl");

                if (returnUrl == null || returnUrl.isBlank()) {
                        returnUrl = request.getContextPath() + "/enrollment/list";
                }

                if (sectionId == null || sectionId.isBlank()) {
                        session.setAttribute("errorMessage", "취소할 분반 정보가 없습니다.");
                        response.sendRedirect(returnUrl);
                        return;
                }

                EnrollmentDAO enrollmentDAO = new EnrollmentDAO();

                try {
                        int deleted = enrollmentDAO.deleteEnrollment(studentId, sectionId);
                        if (deleted > 0) {
                                session.setAttribute("successMessage", "수강 등록을 취소했습니다.");
                        } else {
                                session.setAttribute("errorMessage", "등록 목록에 존재하지 않는 분반입니다.");
                        }
                } catch (SQLException e) {
                        e.printStackTrace();
                        session.setAttribute("errorMessage", "취소 중 오류가 발생했습니다: " + e.getMessage());
                }

                response.sendRedirect(returnUrl);
        }
}
