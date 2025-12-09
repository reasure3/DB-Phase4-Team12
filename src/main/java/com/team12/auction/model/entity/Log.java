package com.team12.auction.model.entity;

import java.sql.Date;

public class Log {
	private String logId;
	private String actionType;
	private Date timestamp;
	private String details;
	private int studentId;
	private String auctionId;

	public Log() {
	}

	public Log(String logId, String actionType, Date timestamp, String details, int studentId, String auctionId) {
		this.logId = logId;
		this.actionType = actionType;
		this.timestamp = timestamp;
		this.details = details;
		this.studentId = studentId;
		this.auctionId = auctionId;
	}

	public String getLogId() {
		return logId;
	}

	public String getActionType() {
		return actionType;
	}

	public Date getTimestamp() {
		return timestamp;
	}

	public String getDetails() {
		return details;
	}

	public int getStudentId() {
		return studentId;
	}

	public String getAuctionId() {
		return auctionId;
	}

	public void setLogId(String logId) {
		this.logId = logId;
	}

	public void setActionType(String actionType) {
		this.actionType = actionType;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

	public void setDetails(String details) {
		this.details = details;
	}

	public void setStudentId(int studentId) {
		this.studentId = studentId;
	}

	public void setAuctionId(String auctionId) {
		this.auctionId = auctionId;
	}

	@Override
	public String toString() {
		return "Log{" + "logId='" + logId + '\'' + ", actionType='" + actionType + '\'' + ", timestamp=" + timestamp
				+ ", details='" + details + '\'' + ", studentId=" + studentId + ", auctionId='" + auctionId + '\''
				+ '}';
	}
}
