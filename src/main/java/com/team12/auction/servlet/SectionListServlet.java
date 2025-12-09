package com.team12.auction.servlet;

import com.team12.auction.dao.SectionDAO;
import com.team12.auction.model.dto.SectionSearchResult;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/section/list")
public class SectionListServlet extends HttpServlet {

    private SectionDAO sectionDAO;

    @Override
    public void init() throws ServletException {
        sectionDAO = new SectionDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        // 로그인 확인 (loginCheck.jsp로도 하지만, 두 번 막는 건 나쁠 것 없음)
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("studentId") == null) {
            response.sendRedirect(request.getContextPath() + "/auth/login");
            return;
        }

        String keyword = request.getParameter("keyword");
        String department = request.getParameter("department");

        try {
            List<SectionSearchResult> sections =
                    sectionDAO.searchSections(keyword, department);

            request.setAttribute("sections", sections);
            request.setAttribute("keyword", keyword);
            request.setAttribute("department", department);

        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("sections", null);
            request.setAttribute("errorMessage", "강의 목록을 불러오는 중 오류가 발생했습니다.");
        }

        request.getRequestDispatcher("/section/sectionList.jsp")
               .forward(request, response);
    }
}
