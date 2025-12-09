package com.team12.auction.servlet;

import com.team12.auction.dao.BasketDAO;
import com.team12.auction.dao.SectionDAO;
import com.team12.auction.dao.StudentDAO;
import com.team12.auction.model.entity.Student;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/basket/add")
public class BasketAddServlet extends HttpServlet {
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
//        if (returnUrl == null || returnUrl.isBlank()) {
//            returnUrl = request.getContextPath() + "/section/list";
//        }

		returnUrl = request.getContextPath() + "/section/list";

		if (sectionId == null || sectionId.isBlank()) {
			session.setAttribute("errorMessage", "담을 분반 정보가 없습니다.");
			response.sendRedirect(returnUrl);
			return;
		}

		BasketDAO basketDAO = new BasketDAO();
		StudentDAO studentDAO = new StudentDAO();
		SectionDAO sectionDAO = new SectionDAO();

		try {
			basketDAO.ensureBasketExists(studentId);

			if (basketDAO.isSectionInBasket(studentId, sectionId)) {
				session.setAttribute("errorMessage", "이미 수강꾸러미에 담은 분반입니다.");
			} else {
				// 학점 제한 체크
				Student student = studentDAO.selectByStudentId(studentId);
				if (student == null) {
					session.setAttribute("errorMessage", "학생 정보를 찾을 수 없습니다.");
					response.sendRedirect(returnUrl);
					return;
				}

				int maxCredits = student.getMaxCredits();
				int currentCredits = basketDAO.getTotalCreditsInBasket(studentId);
				
				// 추가하려는 과목의 학점 조회
				int newCourseCredits = sectionDAO.getCourseCredits(sectionId);
				
				if (currentCredits + newCourseCredits > maxCredits) {
					session.setAttribute("errorMessage", 
							"학점 제한을 초과합니다. (현재: " + currentCredits + "학점, 추가: " + newCourseCredits + "학점, 제한: " + maxCredits + "학점)");
				} else {
					basketDAO.addSectionToBasket(studentId, sectionId);
					session.setAttribute("successMessage", "분반을 수강꾸러미에 담았습니다.");
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
			session.setAttribute("errorMessage", "장바구니 담기에 실패했습니다: " + e.getMessage());
		}

		response.sendRedirect(returnUrl);
	}
}
