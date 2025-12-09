package com.team12.auction.servlet;

import com.team12.auction.dao.LogDAO;
import com.team12.auction.dao.StudentDAO;
import com.team12.auction.model.entity.Log;
import com.team12.auction.model.entity.Student;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;

@WebServlet("/auth/changePassword")
public class ChangePasswordServlet extends HttpServlet {
    private StudentDAO studentDAO;
    private LogDAO logDAO;

    @Override
    public void init() throws ServletException {
        super.init();
        studentDAO = new StudentDAO();
        logDAO = new LogDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("studentId") == null) {
            response.sendRedirect(request.getContextPath() + "/auth/login");
            return;
        }

        request.getRequestDispatcher("/auth/changePassword.jsp").forward(request, response);
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

        Integer studentId = (Integer) session.getAttribute("studentId");

        try {
            Student student = studentDAO.selectById(studentId);
            if (student == null) {
                response.sendRedirect(request.getContextPath() + "/auth/login");
                return;
            }

            // Get form data
            String currentPassword = request.getParameter("currentPassword");
            String newPassword = request.getParameter("newPassword");
            String confirmPassword = request.getParameter("confirmPassword");

            // Validate input
            if (currentPassword == null || currentPassword.trim().isEmpty() ||
                newPassword == null || newPassword.trim().isEmpty() ||
                confirmPassword == null || confirmPassword.trim().isEmpty()) {

                request.setAttribute("errorMessage", "모든 필드를 입력해주세요.");
                request.getRequestDispatcher("/auth/changePassword.jsp").forward(request, response);
                return;
            }

            // Verify current password
            boolean verifyStudent = studentDAO.validateLogin(studentId, currentPassword);
            if (!verifyStudent) {
                request.setAttribute("errorMessage", "현재 비밀번호가 일치하지 않습니다.");
                request.getRequestDispatcher("/auth/changePassword.jsp").forward(request, response);
                return;
            }

            // Check if new passwords match
            if (!newPassword.equals(confirmPassword)) {
                request.setAttribute("errorMessage", "새 비밀번호가 일치하지 않습니다.");
                request.getRequestDispatcher("/auth/changePassword.jsp").forward(request, response);
                return;
            }

            // Check if new password is different from current
            if (currentPassword.equals(newPassword)) {
                request.setAttribute("errorMessage", "새 비밀번호는 현재 비밀번호와 달라야 합니다.");
                request.getRequestDispatcher("/auth/changePassword.jsp").forward(request, response);
                return;
            }

            // Password strength check (optional)
            if (newPassword.length() < 4) {
                request.setAttribute("errorMessage", "비밀번호는 최소 4자 이상이어야 합니다.");
                request.getRequestDispatcher("/auth/changePassword.jsp").forward(request, response);
                return;
            }

            // Update password
            boolean success = studentDAO.changePassword(studentId, newPassword);

            if (success) {
                // Log the action
                Log log = new Log();
                log.setActionType("PASSWORD_CHANGE");
                log.setDetails("Student " + studentId + " changed password");
                log.setStudentId(studentId);
                log.setTimestamp(Timestamp.valueOf(LocalDateTime.now()));
                logDAO.insertLog(log);

                session.setAttribute("successMessage", "비밀번호 변경에 성공했습니다.");
                response.sendRedirect(request.getContextPath() + "/mypage");
            } else {
                request.setAttribute("errorMessage", "비밀번호 변경에 실패했습니다.");
                request.getRequestDispatcher("/auth/changePassword.jsp").forward(request, response);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "데이터베이스 오류가 발생했습니다.");
            request.getRequestDispatcher("/error.jsp").forward(request, response);
        }
    }
}
