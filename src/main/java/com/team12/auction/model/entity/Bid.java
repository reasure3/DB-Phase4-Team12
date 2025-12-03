package com.team12.auction.model.entity;

import java.sql.Date;

public class Bid {
    private String bidSequence;
    private int bidAmount;
    private Date bidTime;
    private String isSuccessful;  // 'Y' 또는 'N'
    private String auctionId;
    private int studentId;

    public Bid() {}

    public Bid(String bidSequence, int bidAmount, Date bidTime,
               String isSuccessful, String auctionId, int studentId) {
        this.bidSequence = bidSequence;
        this.bidAmount = bidAmount;
        this.bidTime = bidTime;
        this.isSuccessful = isSuccessful;
        this.auctionId = auctionId;
        this.studentId = studentId;
    }

    public String getBidSequence() { return bidSequence; }
    public int getBidAmount() { return bidAmount; }
    public Date getBidTime() { return bidTime; }
    public String getIsSuccessful() { return isSuccessful; }
    public String getAuctionId() { return auctionId; }
    public int getStudentId() { return studentId; }

    public void setBidSequence(String bidSequence) { this.bidSequence = bidSequence; }
    public void setBidAmount(int bidAmount) { this.bidAmount = bidAmount; }
    public void setBidTime(Date bidTime) { this.bidTime = bidTime; }
    public void setIsSuccessful(String isSuccessful) { this.isSuccessful = isSuccessful; }
    public void setAuctionId(String auctionId) { this.auctionId = auctionId; }
    public void setStudentId(int studentId) { this.studentId = studentId; }

    @Override
    public String toString() {
        return "Bid{" +
                "bidSequence='" + bidSequence + '\'' +
                ", bidAmount=" + bidAmount +
                ", bidTime=" + bidTime +
                ", isSuccessful='" + isSuccessful + '\'' +
                ", auctionId='" + auctionId + '\'' +
                ", studentId=" + studentId +
                '}';
    }
}

