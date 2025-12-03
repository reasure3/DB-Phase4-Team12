package com.team12.auction.model.dto;

import java.sql.Date;

import com.team12.auction.model.entity.Bid;

public class BidDetail extends Bid {
    // JOIN으로 가져올 추가 정보
    private String studentName;
    private String courseName;
    private String sectionId;
	
    public BidDetail() {}

    public BidDetail(String bidSequence, int bidAmount, Date bidTime,
               String isSuccessful, String auctionId, int studentId) {
        super(bidSequence, bidAmount, bidTime, isSuccessful, auctionId, studentId);
    }
    
	public String getStudentName() {
		return studentName;
	}

	public void setStudentName(String studentName) {
		this.studentName = studentName;
	}

	public String getCourseName() {
		return courseName;
	}

	public void setCourseName(String courseName) {
		this.courseName = courseName;
	}

	public String getSectionId() {
		return sectionId;
	}

	public void setSectionId(String sectionId) {
		this.sectionId = sectionId;
	}
}
