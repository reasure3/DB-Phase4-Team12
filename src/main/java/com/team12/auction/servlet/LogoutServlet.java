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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@WebServlet("/auth/logout")
public class LogoutServlet extends HttpServlet {
    @Serial private static final long serialVersionUID = -6317185385750980605L;

    private LogDAO logDAO;

    private ExecutorService executorService;

    @Override
    public void init() throws ServletException {
        logDAO = new LogDAO();

        // 최대 10개의 스레드로 로그 처리
        executorService = Executors.newFixedThreadPool(10);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession(false);
        if (session != null) {
            Integer studentId = (Integer) session.getAttribute("studentId");

            // 세션 무효화 먼저
            session.invalidate();

            // 로그인 페이지로 즉시 리다이렉트
            response.sendRedirect(request.getContextPath() + "/auth/login");

            // 로그는 백그라운드에서 비동기 처리
            if (studentId != null) {
                final Integer finalStudentId = studentId;

                executorService.submit(() -> {
                    try {
                        Log log = new Log();
                        log.setActionType("LOGOUT");
                        log.setDetails("Student " + finalStudentId + " logout");
                        log.setStudentId(finalStudentId);
                        log.setTimestamp(Timestamp.valueOf(LocalDateTime.now()));

                        logDAO.insertLog(log);

                    } catch (SQLException e) {
                        System.err.println("[Async] Failed to insert logout log for student: " + finalStudentId);
                        e.printStackTrace();
                    }
                });
            }
        } else {
            // 세션이 없으면 그냥 로그인 페이지로
            response.sendRedirect(request.getContextPath() + "/auth/login");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        doGet(request, response);
    }
}
