package com.team12.auction.model.entity;

import java.sql.Date;

public class Enrollment {
	private String enrollmentId;
	private String enrollmentSource;
	private int pointsUsed;
	private Date enrollmentTime;
	private int studentId;
	private String sectionId;

	public Enrollment() {
	}

	public Enrollment(String enrollmentId, String enrollmentSource, int pointsUsed, Date enrollmentTime, int studentId,
			String sectionId) {
		this.enrollmentId = enrollmentId;
		this.enrollmentSource = enrollmentSource;
		this.pointsUsed = pointsUsed;
		this.enrollmentTime = enrollmentTime;
		this.studentId = studentId;
		this.sectionId = sectionId;
	}

	public String getEnrollmentId() {
		return enrollmentId;
	}

	public String getEnrollmentSource() {
		return enrollmentSource;
	}

	public int getPointsUsed() {
		return pointsUsed;
	}

	public Date getEnrollmentTime() {
		return enrollmentTime;
	}

	public int getStudentId() {
		return studentId;
	}

	public String getSectionId() {
		return sectionId;
	}

	public void setEnrollmentId(String enrollmentId) {
		this.enrollmentId = enrollmentId;
	}

	public void setEnrollmentSource(String enrollmentSource) {
		this.enrollmentSource = enrollmentSource;
	}

	public void setPointsUsed(int pointsUsed) {
		this.pointsUsed = pointsUsed;
	}

	public void setEnrollmentTime(Date enrollmentTime) {
		this.enrollmentTime = enrollmentTime;
	}

	public void setStudentId(int studentId) {
		this.studentId = studentId;
	}

	public void setSectionId(String sectionId) {
		this.sectionId = sectionId;
	}

	@Override
	public String toString() {
		return "Enrollment{" + "enrollmentId='" + enrollmentId + '\'' + ", enrollmentSource='" + enrollmentSource + '\''
				+ ", pointsUsed=" + pointsUsed + ", enrollmentTime=" + enrollmentTime + ", studentId=" + studentId
				+ ", sectionId='" + sectionId + '\'' + '}';
	}
}
