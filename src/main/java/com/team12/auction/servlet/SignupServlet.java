package com.team12.auction.servlet;

import com.team12.auction.dao.StudentDAO;
import com.team12.auction.model.entity.Student;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.Serial;
import java.sql.SQLException;

@WebServlet("/auth/signup")
public class SignupServlet extends HttpServlet {
	@Serial
	private static final long serialVersionUID = 9155098615938890819L;

	private StudentDAO studentDAO;

	@Override
	public void init() {
		studentDAO = new StudentDAO();
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// GET 요청 시 회원가입 페이지로 이동
		request.getRequestDispatcher("/auth/signup.jsp").forward(request, response);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		// 한글 인코딩 설정
		request.setCharacterEncoding("UTF-8");

		// 파라미터 받기
		String studentIdStr = request.getParameter("studentId");
		String name = request.getParameter("name");
		String department = request.getParameter("department");
		String gradeStr = request.getParameter("grade");
		String password = request.getParameter("password");
		String passwordConfirm = request.getParameter("passwordConfirm");

		// 입력값 검증
		if (studentIdStr == null || studentIdStr.trim().isEmpty() || name == null || name.trim().isEmpty()
				|| department == null || department.trim().isEmpty() || gradeStr == null || gradeStr.trim().isEmpty()
				|| password == null || password.trim().isEmpty() || passwordConfirm == null
				|| passwordConfirm.trim().isEmpty()) {

			request.setAttribute("errorMessage", "모든 필드를 입력해주세요.");
			request.setAttribute("studentId", studentIdStr);
			request.setAttribute("name", name);
			request.setAttribute("department", department);
			request.setAttribute("grade", gradeStr);
			request.getRequestDispatcher("/auth/signup.jsp").forward(request, response);
			return;
		}

		// 비밀번호 확인
		if (!password.equals(passwordConfirm)) {
			request.setAttribute("errorMessage", "비밀번호가 일치하지 않습니다.");
			request.setAttribute("studentId", studentIdStr);
			request.setAttribute("name", name);
			request.setAttribute("department", department);
			request.setAttribute("grade", gradeStr);
			request.getRequestDispatcher("/auth/signup.jsp").forward(request, response);
			return;
		}

		try {
			// 학번과 학년을 int로 변환
			int studentId = Integer.parseInt(studentIdStr.trim());
			int grade = Integer.parseInt(gradeStr.trim());

			// 학년 유효성 검사 (1~4학년)
			if (grade < 1 || grade > 4) {
				request.setAttribute("errorMessage", "학년은 1~4 사이의 숫자여야 합니다.");
				request.setAttribute("studentId", studentIdStr);
				request.setAttribute("name", name);
				request.setAttribute("department", department);
				request.setAttribute("grade", gradeStr);
				request.getRequestDispatcher("/auth/signup.jsp").forward(request, response);
				return;
			}

			// 학번 중복 체크
			if (studentDAO.existsById(studentId)) {
				request.setAttribute("errorMessage", "이미 등록된 학번입니다.");
				request.setAttribute("studentId", studentIdStr);
				request.setAttribute("name", name);
				request.setAttribute("department", department);
				request.setAttribute("grade", gradeStr);
				request.getRequestDispatcher("/auth/signup.jsp").forward(request, response);
				return;
			}

			// Student 객체 생성
			Student student = new Student();
			student.setStudentId(studentId);
			student.setName(name.trim());
			student.setDepartment(department.trim());
			student.setGrade(grade);
			student.setPassword(password);
			student.setMaxCredits(18);
			student.setMaxPoint(18 * 5);

			// 회원가입 처리
			int result = studentDAO.signUp(student);

			if (result > 0) {
				// 회원가입 성공: 로그인 페이지로 리다이렉트
				request.getSession().setAttribute("successMessage", "회원가입이 완료되었습니다. 로그인해주세요.");
				response.sendRedirect(request.getContextPath() + "/auth/login");
			} else {
				// 회원가입 실패
				request.setAttribute("errorMessage", "회원가입에 실패했습니다. 다시 시도해주세요.");
				request.setAttribute("studentId", studentIdStr);
				request.setAttribute("name", name);
				request.setAttribute("department", department);
				request.setAttribute("grade", gradeStr);
				request.getRequestDispatcher("/auth/signup.jsp").forward(request, response);
			}

		} catch (NumberFormatException e) {
			// 학번이나 학년이 숫자가 아닌 경우
			request.setAttribute("errorMessage", "학번과 학년은 숫자로 입력해주세요.");
			request.setAttribute("studentId", studentIdStr);
			request.setAttribute("name", name);
			request.setAttribute("department", department);
			request.setAttribute("grade", gradeStr);
			request.getRequestDispatcher("/auth/signup.jsp").forward(request, response);

		} catch (SQLException e) {
			// DB 오류
			e.printStackTrace();
			request.setAttribute("errorMessage", "시스템 오류가 발생했습니다. 잠시 후 다시 시도해주세요.");
			request.setAttribute("studentId", studentIdStr);
			request.setAttribute("name", name);
			request.setAttribute("department", department);
			request.setAttribute("grade", gradeStr);
			request.getRequestDispatcher("/auth/signup.jsp").forward(request, response);
		}
	}
}