package com.team12.auction.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.team12.auction.model.entity.Course;
import com.team12.auction.model.entity.Section;

public class SectionDAO extends BaseDao {
	public SectionDAO(Connection conn) {
		super(conn);
	}

	// 1. 강의 코드로 분반 조회
	public List<Section> selectByCourseId(String CourseId) throws SQLException {
		String sql = "SELECT s.section_id, s.section_number, s.professor, s.capacity, s.classroom " +
	                 "FROM SECTION s " +
				     "JOIN COURSE c ON s.course_id = c.course_id " +
	                 "WHERE s.course_id = ? " +
	                 "ORDER BY s.section_number";

		List<Section> sections = new ArrayList<Section>();

		try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setString(1, CourseId);

			try (ResultSet rs = pstmt.executeQuery()) {
				while (rs.next()) {
					Section section = new Section();
					section.setSectionId(rs.getString(1));
					section.setSectionNumber(rs.getInt(2));
					section.setProfessor(rs.getString(3));
					section.setCapacity(rs.getInt(4));
					section.setClassroom(rs.getString(5));
					section.setCourseId(CourseId);
					sections.add(section);
				}
			} catch(Exception e) {
				e.printStackTrace();
			}
		}

		return sections;
	}
	
	// 나의 분반 조회
	// 각 강의 당 한 분반만 등록 가능하다고 가정
	public Map<Course, Section> selectMySection(int studentId) throws SQLException {
	    String sql =
	            "SELECT DISTINCT " +
	            "    s.section_id, s.section_number, s.professor, s.capacity, s.classroom, " +
	            "    c.course_id, c.course_name, c.department, c.credits, c.semester, c.year " +
	            "FROM ENROLLMENT e " +
	            "JOIN SECTION s ON e.section_id = s.section_id " +
	            "JOIN COURSE c ON s.course_id = c.course_id " +
	            "WHERE e.student_id = ? " +
	            "ORDER BY c.course_id, s.section_number";
        
        Map<Course, Section> courseSectionMap = new HashMap<>();
        
		try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setInt(1, studentId);
			try (ResultSet rs = pstmt.executeQuery()) {
				while (rs.next()) {
					// Section 객체 생성
					Section section = new Section();
					section.setSectionId(rs.getString(1));
					section.setSectionNumber(rs.getInt(2));
					section.setProfessor(rs.getString(3));
					section.setCapacity(rs.getInt(4));
					section.setClassroom(rs.getString(5));
					
					// Course 객체 생성
					Course course = new Course();
					course.setCourseId(rs.getString(6));
					course.setCourseName(rs.getString(7));
					course.setDepartment(rs.getString(8));
					course.setCredits(rs.getInt(9));
					course.setSemester(rs.getString(10));
					course.setYear(rs.getInt(11));

//					section.setCourseId(rs.getString(6));

					// Map에 추가
					courseSectionMap.put(course, section);
				}
			}
		}
        
        return courseSectionMap;
    }
	
	// 분반의 현재 등록 인원 조회
	public int getCurrentEnrollment(String sectionId) throws SQLException {
        String sql = 
            "SELECT COUNT(*) as cnt " +
            "FROM ENROLLMENT " +
            "WHERE section_id = ?";
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, sectionId);
            try (ResultSet rs = pstmt.executeQuery()) {
            	if (rs.next()) {
                    return rs.getInt(1);
                }
            }
            
            
            return 0;
        }
    }
}
