package com.team12.auction.model.entity;

public class Student {
    private int studentId;
    private String name;
    private String department;
    private int grade;
    private String password;
    private int maxCredits;
    private int maxPoint;

    public Student() {}

    public Student(int studentId, String name, String department,
                   int grade, String password, int maxCredits, int maxPoint) {
        this.studentId = studentId;
        this.name = name;
        this.department = department;
        this.grade = grade;
        this.password = password;
        this.maxCredits = maxCredits;
        this.maxPoint = maxPoint;
    }

    // getter / setter (필요한 만큼만 만들어도 됨)
    public int getStudentId() { return studentId; }
    public void setStudentId(int studentId) { this.studentId = studentId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public int getGrade() { return grade; }
    public void setGrade(int grade) { this.grade = grade; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public int getMaxCredits() { return maxCredits; }
    public void setMaxCredits(int maxCredits) { this.maxCredits = maxCredits; }

    public int getMaxPoint() { return maxPoint; }
    public void setMaxPoint(int maxPoint) { this.maxPoint = maxPoint; }

    @Override
    public String toString() {
        return "Student{" +
                "studentId=" + studentId +
                ", name='" + name + '\'' +
                ", department='" + department + '\'' +
                ", grade=" + grade +
                ", maxCredits=" + maxCredits +
                ", maxPoint=" + maxPoint +
                '}';
    }

    public String info() {
        return "Student{" +
                "studentId=" + studentId +
                ", name='" + name + '\'' +
                ", department='" + department + '\'' +
                ", grade=" + grade +
                '}';
    }
}
