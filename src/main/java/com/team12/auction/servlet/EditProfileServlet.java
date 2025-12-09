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

@WebServlet("/editProfile")
public class EditProfileServlet extends HttpServlet {
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
            Student student = studentDAO.selectById(studentId);
            if (student == null) {
                response.sendRedirect("/auth/login");
                return;
            }

            request.setAttribute("student", student);
            request.getRequestDispatcher("/student/editProfile.jsp").forward(request, response);

        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "정보를 불러오는 중 오류가 발생했습니다.");
            request.getRequestDispatcher("/error.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("studentId") == null) {
            response.sendRedirect("/auth/login");
            return;
        }

        Integer studentId = (Integer) session.getAttribute("studentId");

        try {
            Student student = studentDAO.selectById(studentId);
            if (student == null) {
                response.sendRedirect("/auth/login");
                return;
            }

            // Get form data
            String name = request.getParameter("name");
            String department = request.getParameter("department");
            String gradeStr = request.getParameter("grade");

            // Validate input
            if (name == null || name.trim().isEmpty() ||
                department == null || department.trim().isEmpty() ||
                gradeStr == null || gradeStr.trim().isEmpty()) {

                request.setAttribute("errorMessage", "모든 필드를 입력해주세요.");
                request.setAttribute("student", student);
                request.getRequestDispatcher("/student/editProfile.jsp").forward(request, response);
                return;
            }

            int grade;
            try {
                grade = Integer.parseInt(gradeStr);
                if (grade < 1 || grade > 4) {
                    throw new NumberFormatException();
                }
            } catch (NumberFormatException e) {
                request.setAttribute("errorMessage", "학년은 1~4 사이의 숫자여야 합니다.");
                request.setAttribute("student", student);
                request.getRequestDispatcher("/student/editProfile.jsp").forward(request, response);
                return;
            }

            // Update student info
            student.setName(name);
            student.setDepartment(department);
            student.setGrade(grade);

            boolean success = studentDAO.updateStudent(student);

            if (success) {
                // Update session name
                session.setAttribute("studentName", name);

                // Log the action
                Log log = new Log();
                log.setActionType("PROFILE_UPDATE");
                log.setDetails("프로필 정보 수정");
                log.setStudentId(studentId);
                log.setTimestamp(Timestamp.valueOf(LocalDateTime.now()));
                logDAO.insertLog(log);

                session.setAttribute("successMessage", "프로필 업데이트에 성공했습니다.");
                response.sendRedirect(request.getContextPath() + "/mypage");
            } else {
                request.setAttribute("errorMessage", "정보 수정에 실패했습니다.");
                request.setAttribute("student", student);
                request.getRequestDispatcher("/student/editProfile.jsp").forward(request, response);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "데이터베이스 오류가 발생했습니다.");
            request.getRequestDispatcher("/error.jsp").forward(request, response);
        }
    }
}
