package com.team12.auction.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.team12.auction.model.entity.Course;

public class CourseDAO extends BaseDao {
	public CourseDAO(Connection conn) {
		super(conn);
	}

	public Course selectById(String courseId) throws SQLException {
		String sql = "SELECT course_id, course_name, department, credits, capacity, semester, year " + "FROM COURSE "
				+ "WHERE course_id = ?";

		try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setString(1, courseId);
			try (ResultSet rs = pstmt.executeQuery()) {
				if (rs.next()) {
					Course course = new Course();
					course.setCourseId(rs.getString(1));
					course.setCourseName(rs.getString(2));
					course.setDepartment(rs.getString(3));
					course.setCredits(rs.getInt(4));
					course.setCapacity(rs.getInt(5));
					course.setSemester(rs.getString(6));
					course.setYear(rs.getInt(7));
					return course;
				}
			}
			return null;
		}
	}
	
	// 전체 강의 리스트 반환
    public List<Course> getAllCourses() throws SQLException {
        String sql = "SELECT course_id, course_name, department, credits, " +
                     "       capacity, semester, year " +
                     "FROM Course " +
                     "ORDER BY course_id";

        List<Course> list = new ArrayList<>();

        try (PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                Course c = new Course();
                c.setCourseId(rs.getString(1));
                c.setCourseName(rs.getString(2));
                c.setDepartment(rs.getString(3));
                c.setCredits(rs.getInt(4));
                c.setCapacity(rs.getInt(5));
                c.setSemester(rs.getString(6));
                c.setYear(rs.getInt(7));

                list.add(c);
            }
        }
        return list;
    }

    // 콘솔에 전체 강의 출력
    public void printAllCourses() throws SQLException {
        List<Course> courses = getAllCourses();

        if (courses.isEmpty()) {
            System.out.println("등록된 강의가 없습니다.");
            return;
        }

        System.out.printf("%-10s %-25s %-15s %-7s %-8s %-8s %-6s\n",
                "ID", "COURSE_NAME", "DEPT", "CREDITS", "CAP", "SEM", "YEAR");
        System.out.println("----------------------------------------------------------------------------------------");

        for (Course c : courses) {
            System.out.printf("%-10s %-25s %-15s %-7d %-8d %-8s %-6d\n",
                    c.getCourseId(),
                    c.getCourseName(),
                    c.getDepartment(),
                    c.getCredits(),
                    c.getCapacity(),
                    c.getSemester(),
                    c.getYear());
        }
    }
    
    

    public void printMyCourses(long studentId) throws SQLException {
        String sql =
            "SELECT DISTINCT c.course_id, c.course_name, c.department, c.credits, " +
            "                c.capacity, c.semester, c.year " +
            "FROM Enrollment e " +
            "JOIN Section s ON e.section_id = s.section_id " +
            "JOIN Course c ON s.course_id = c.course_id " +
            "WHERE e.student_id = ? " +
            "ORDER BY c.course_id";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, studentId);

            try (ResultSet rs = pstmt.executeQuery()) {

                if (!rs.isBeforeFirst()) {
                    System.out.println("현재 수강 중인 강의가 없습니다.");
                    return;
                }

                System.out.printf("%-10s %-25s %-15s %-7s %-8s %-8s %-6s\n",
                        "ID", "COURSE_NAME", "DEPT", "CRED", "CAP", "SEM", "YEAR");
                System.out.println("----------------------------------------------------------------------------------------------");

                while (rs.next()) {
                    System.out.printf("%-10s %-25s %-15s %-7d %-8d %-8s %-6d\n",
                            rs.getString(1),
                            rs.getString(2),
                            rs.getString(3),
                            rs.getInt(4),
                            rs.getInt(5),
                            rs.getString(6),
                            rs.getInt(7));
                }
            }
        }
    }
    
    
    // 학과별 강의 조회 (Course 속성만)
    public void printCoursesByDepartment(String department) throws SQLException {
        String sql =
            "SELECT course_id, course_name, department, credits, " +
            "       capacity, semester, year " +
            "FROM Course " +
            "WHERE department = ? " +
            "ORDER BY course_id";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, department);

            try (ResultSet rs = pstmt.executeQuery()) {

                if (!rs.isBeforeFirst()) { // 결과가 비었는지 확인
                    System.out.println("해당 학과의 강의가 없습니다: " + department);
                    return;
                }

                System.out.printf("%-10s %-25s %-15s %-7s %-8s %-8s %-6s\n",
                        "ID", "COURSE_NAME", "DEPT", "CRED", "CAP", "SEM", "YEAR");
                System.out.println("-------------------------------------------------------------------------------------------");

                while (rs.next()) {
                    System.out.printf("%-10s %-25s %-15s %-7d %-8d %-8s %-6d\n",
                            rs.getString(1),
                            rs.getString(2),
                            rs.getString(3),
                            rs.getInt(4),
                            rs.getInt((5)),
                            rs.getString(6),
                            rs.getInt(7));
                }
            }
        }
    }
}
