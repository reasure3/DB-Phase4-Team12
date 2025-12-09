package com.team12.auction.servlet;

import com.team12.auction.dao.LogDAO;
import com.team12.auction.model.entity.Log;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.io.Serial;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;

@WebServlet("/auth/logout")
public class LogoutServlet extends HttpServlet {
    @Serial private static final long serialVersionUID = -6317185385750980605L;

    private LogDAO logDAO;

    @Override
    public void init() throws ServletException {
        logDAO = new LogDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // 세션 무효화
        HttpSession session = request.getSession(false);
        try {
            if (session != null) {
                Integer studentId = (Integer) session.getAttribute("studentId");
                if (studentId != null) {
                    Log log = new Log();
                    log.setActionType("LOGOUT");
                    log.setDetails("Student " + studentId + " logout");
                    log.setStudentId(studentId);
                    log.setTimestamp(Timestamp.valueOf(LocalDateTime.now()));
                    logDAO.insertLog(log);
                }

                session.invalidate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // 로그인 페이지로 리다이렉트
        response.sendRedirect(request.getContextPath() + "/auth/login");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }
}
