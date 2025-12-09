package com.team12.auction.model.entity;

public class Section {
	private String sectionId;
	private int sectionNumber;
	private String professor;
	private int capacity;
	private String classroom;
	private String courseId;

	public Section() {
	}

	public Section(String sectionId, int sectionNumber, String professor, int capacity, String classroom,
			String courseId) {
		this.sectionId = sectionId;
		this.sectionNumber = sectionNumber;
		this.professor = professor;
		this.capacity = capacity;
		this.classroom = classroom;
		this.courseId = courseId;
	}

	public String getSectionId() {
		return sectionId;
	}

	public int getSectionNumber() {
		return sectionNumber;
	}

	public String getProfessor() {
		return professor;
	}

	public int getCapacity() {
		return capacity;
	}

	public String getClassroom() {
		return classroom;
	}

	public String getCourseId() {
		return courseId;
	}

	public void setSectionId(String sectionId) {
		this.sectionId = sectionId;
	}

	public void setSectionNumber(int sectionNumber) {
		this.sectionNumber = sectionNumber;
	}

	public void setProfessor(String professor) {
		this.professor = professor;
	}

	public void setCapacity(int capacity) {
		this.capacity = capacity;
	}

	public void setClassroom(String classroom) {
		this.classroom = classroom;
	}

	public void setCourseId(String courseId) {
		this.courseId = courseId;
	}

	@Override
	public String toString() {
		return "Section{" + "sectionId='" + sectionId + '\'' + ", sectionNumber=" + sectionNumber + ", professor='"
				+ professor + '\'' + ", capacity=" + capacity + ", classroom='" + classroom + '\'' + ", courseId='"
				+ courseId + '\'' + '}';
	}
}
