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
import java.util.List;

@WebServlet("/mypage")
public class MyPageServlet extends HttpServlet {
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
            response.sendRedirect("/auth/login");
            return;
        }

        Integer studentId = (Integer) session.getAttribute("studentId");

        try {
            // Get student information
            Student student = studentDAO.selectById(studentId);
            if (student == null) {
                response.sendRedirect("/auth/login");
                return;
            }

            // Get current enrolled credits and points
            int currentCredits = studentDAO.getCurrentCredits(studentId);
            int currentPoints = studentDAO.getCurrentPoints(studentId);

            // Get recent activity logs (last 10)
            List<Log> recentLogs = logDAO.getRecentLogsByStudent(studentId, 10);

            // Set attributes for JSP
            request.setAttribute("student", student);
            request.setAttribute("currentCredits", currentCredits);
            request.setAttribute("currentPoints", currentPoints);
            request.setAttribute("recentLogs", recentLogs);

            // Forward to mypage.jsp
            request.getRequestDispatcher("/student/mypage.jsp").forward(request, response);

        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "정보를 불러오는 중 오류가 발생했습니다.");
            request.getRequestDispatcher("/error.jsp").forward(request, response);
        }
    }
}
