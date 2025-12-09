package com.team12.auction.model.entity;

public class TimeSlot {
	private String day;
	private int startTime;
	private int endTime;
	private String sectionId;

	public TimeSlot() {
	}

	public TimeSlot(String day, int startTime, int endTime, String sectionId) {
		this.day = day;
		this.startTime = startTime;
		this.endTime = endTime;
		this.sectionId = sectionId;
	}

	public String getDay() {
		return day;
	}

	public int getStartTime() {
		return startTime;
	}

	public int getEndTime() {
		return endTime;
	}

	public String getSectionId() {
		return sectionId;
	}

	public void setDay(String day) {
		this.day = day;
	}

	public void setStartTime(int startTime) {
		this.startTime = startTime;
	}

	public void setEndTime(int endTime) {
		this.endTime = endTime;
	}

	public void setSectionId(String sectionId) {
		this.sectionId = sectionId;
	}

	@Override
	public String toString() {
		return "TimeSlot{" + "day='" + day + '\'' + ", startTime=" + startTime + ", endTime=" + endTime
				+ ", sectionId='" + sectionId + '\'' + '}';
	}
}
