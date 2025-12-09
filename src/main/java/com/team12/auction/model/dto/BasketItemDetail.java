package com.team12.auction.model.dto;

public class BasketItemDetail {
	private String sectionId;
	private int sectionNumber;
	private String professor;
	private int capacity;
	private String classroom;
	private String courseId;
	private String courseName;
	private int credits;
	private String status;
	private String reason;
	private java.sql.Timestamp registrationTime;
	private java.sql.Timestamp processedTime;
	private int basketCount; // 해당 분반을 장바구니에 담은 인원 수

	// Getter & Setter
	public String getSectionId() {
		return sectionId;
	}

	public void setSectionId(String sectionId) {
		this.sectionId = sectionId;
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

	public int getCapacity() {
		return capacity;
	}

	public void setCapacity(int capacity) {
		this.capacity = capacity;
	}

	public String getClassroom() {
		return classroom;
	}

	public void setClassroom(String classroom) {
		this.classroom = classroom;
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

	public int getCredits() {
		return credits;
	}

	public void setCredits(int credits) {
		this.credits = credits;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public java.sql.Timestamp getRegistrationTime() {
		return registrationTime;
	}

	public void setRegistrationTime(java.sql.Timestamp registrationTime) {
		this.registrationTime = registrationTime;
	}

	public java.sql.Timestamp getProcessedTime() {
		return processedTime;
	}

	public void setProcessedTime(java.sql.Timestamp processedTime) {
		this.processedTime = processedTime;
	}

	public int getBasketCount() {
		return basketCount;
	}

	public void setBasketCount(int basketCount) {
		this.basketCount = basketCount;
	}
}
