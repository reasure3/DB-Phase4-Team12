package com.team12.auction.model.entity;

import java.util.Objects;

public class Course {

    private String courseId;      // COURSE_ID
    private String courseName;    // COURSE_NAME
    private String department;    // DEPARTMENT
    private int credits;          // CREDITS
    private int capacity;         // CAPACITY
    private String semester;      // SEMESTER
    private int year;             // YEAR

    // 기본 생성자
    public Course() {}

    // 모든 필드를 받는 생성자
    public Course(String courseId, String courseName, String department,
                  int credits, int capacity, String semester, int year) {
        this.courseId = courseId;
        this.courseName = courseName;
        this.department = department;
        this.credits = credits;
        this.capacity = capacity;
        this.semester = semester;
        this.year = year;
    }

    // Getter & Setter
    public String getCourseId() {return courseId;}
    public void setCourseId(String courseId) {this.courseId = courseId;}
    
    public String getCourseName() {return courseName;}
    public void setCourseName(String courseName) {this.courseName = courseName;}

    public String getDepartment() {return department;}
    public void setDepartment(String department) {this.department = department;}

    public int getCredits() {return credits;}
    public void setCredits(int credits) {this.credits = credits;}

    public int getCapacity() {return capacity;}
    public void setCapacity(int capacity) {this.capacity = capacity;}

    public String getSemester() {return semester;}
    public void setSemester(String semester) {this.semester = semester;}

    public int getYear() {return year;}
    public void setYear(int year) {this.year = year;}

    @Override
    public String toString() {
        return "Course{" +
                "courseId='" + courseId + '\'' +
                ", courseName='" + courseName + '\'' +
                ", department='" + department + '\'' +
                ", credits=" + credits +
                ", capacity=" + capacity +
                ", semester='" + semester + '\'' +
                ", year=" + year +
                '}';
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;  // 같은 참조면 true
        if (o == null || getClass() != o.getClass()) return false;  // null이거나 다른 클래스면 false
        
        Course course = (Course) o;
        
        // courseId만으로 비교 (PK이므로)
        return Objects.equals(courseId, course.courseId);
    }
    
    @Override
    public int hashCode() {
        // courseId만으로 해시 생성 (PK이므로)
        return Objects.hash(courseId);
    }
}

