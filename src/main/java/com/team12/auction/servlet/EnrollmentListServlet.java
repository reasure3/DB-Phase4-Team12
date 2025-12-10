package com.team12.auction.servlet;

import com.team12.auction.dao.EnrollmentDAO;
import com.team12.auction.model.dto.EnrollmentDetail;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/enrollment/list")
public class EnrollmentListServlet extends HttpServlet {

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

                try {
                        int studentId = (Integer) session.getAttribute("studentId");
                        List<EnrollmentDetail> enrollments = enrollmentDAO.getMyEnrollment(studentId);
                        request.setAttribute("enrollments", enrollments);
                } catch (SQLException e) {
                        e.printStackTrace();
                        request.setAttribute("enrollments", new ArrayList<EnrollmentDetail>());
                        request.setAttribute("errorMessage", "등록 정보를 불러오는 중 오류가 발생했습니다.");
                }

                request.getRequestDispatcher("/enrollment/myEnrollment.jsp").forward(request, response);
        }
}
