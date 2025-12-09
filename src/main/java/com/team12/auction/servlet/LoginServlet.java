package com.team12.auction.servlet;

import com.team12.auction.dao.StudentDAO;
import com.team12.auction.model.entity.Student;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.io.Serial;
import java.sql.SQLException;

@WebServlet("/auth/login")
public class LoginServlet extends HttpServlet {
	@Serial
	private static final long serialVersionUID = -1705710102025229792L;

	private StudentDAO studentDAO;

	@Override
	public void init() throws ServletException {
		studentDAO = new StudentDAO();
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// GET 요청 시 로그인 페이지로 이동
		request.getRequestDispatcher("/auth/login.jsp").forward(request, response);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// 한글 인코딩 설정
		request.setCharacterEncoding("UTF-8");

		// 파라미터 받기
		String studentIdStr = request.getParameter("studentId");
		String password = request.getParameter("password");

		// 입력값 검증
		if (studentIdStr == null || studentIdStr.trim().isEmpty() || password == null || password.trim().isEmpty()) {
			request.setAttribute("errorMessage", "학번과 비밀번호를 모두 입력해주세요.");
			request.getRequestDispatcher("/auth/login.jsp").forward(request, response);
			return;
		}

		try {
			// 학번을 int로 변환
			int studentId = Integer.parseInt(studentIdStr.trim());

			// 로그인 처리
			Student student = studentDAO.login(studentId, password);

			if (student != null) {
				// 로그인 성공: 세션에 student_id 저장
				HttpSession session = request.getSession();
				session.setAttribute("studentId", student.getStudentId());
				session.setAttribute("studentName", student.getName());

				// main.jsp로 리다이렉트
				response.sendRedirect(request.getContextPath() + "/main.jsp");
			} else {
				// 로그인 실패: 에러 메시지와 함께 login.jsp로 포워드
				request.setAttribute("errorMessage", "학번 또는 비밀번호가 올바르지 않습니다.");
				request.setAttribute("studentId", studentIdStr);
				request.getRequestDispatcher("/auth/login.jsp").forward(request, response);
			}

		} catch (NumberFormatException e) {
			// 학번이 숫자가 아닌 경우
			request.setAttribute("errorMessage", "학번은 숫자로 입력해주세요.");
			request.setAttribute("studentId", studentIdStr);
			request.getRequestDispatcher("/auth/login.jsp").forward(request, response);

		} catch (SQLException e) {
			// DB 오류
			e.printStackTrace();
			request.setAttribute("errorMessage", "시스템 오류가 발생했습니다. 잠시 후 다시 시도해주세요.");
			request.setAttribute("studentId", studentIdStr);
			request.getRequestDispatcher("/auth/login.jsp").forward(request, response);
		}
	}
}
