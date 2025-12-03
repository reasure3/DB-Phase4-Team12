package com.team12.auction.model.dto;

import java.sql.Date;

import com.team12.auction.model.entity.Auction;

public class AuctionDetail extends Auction {
    // JOIN으로 가져올 추가 정보
    private String courseId;
    private String courseName;
    private String department;
    private int credits;
    private int sectionNumber;
    private String professor;

    public AuctionDetail() {}

    public AuctionDetail(String auctionId, Date startTime, Date endTime,
                   String status, int availableSlots, Date createdAt, String sectionId) {
        super(auctionId, startTime, endTime, status, availableSlots, createdAt, sectionId);
    }
    
	public String getCourseId() {
		return courseId;
	}

	public void setCourseId(String courseId) {
		this.courseId = courseId;
	}

	public String getCourseName() {
		return courseName;
	}

	public void setCourseName(String courseName) {
		this.courseName = courseName;
	}

	public String getDepartment() {
		return department;
	}

	public void setDepartment(String department) {
		this.department = department;
	}

	public int getCredits() {
		return credits;
	}

	public void setCredits(int credits) {
		this.credits = credits;
	}

	public int getSectionNumber() {
		return sectionNumber;
	}

	public void setSectionNumber(int sectionNumber) {
		this.sectionNumber = sectionNumber;
	}

	public String getProfessor() {
		return professor;
	}

	public void setProfessor(String professor) {
		this.professor = professor;
	}
}
