package com.team12.auction.model.entity;

import java.sql.Date;

public class Auction {
    private String auctionId;
    private Date startTime;
    private Date endTime;
    private String status;
    private int availableSlots;
    private Date createdAt;
    private String sectionId;

    public Auction() {}

    public Auction(String auctionId, Date startTime, Date endTime,
                   String status, int availableSlots, Date createdAt, String sectionId) {
        this.auctionId = auctionId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.status = status;
        this.availableSlots = availableSlots;
        this.createdAt = createdAt;
        this.sectionId = sectionId;
    }

    public String getAuctionId() { return auctionId; }
    public Date getStartTime() { return startTime; }
    public Date getEndTime() { return endTime; }
    public String getStatus() { return status; }
    public int getAvailableSlots() { return availableSlots; }
    public Date getCreatedAt() { return createdAt; }
    public String getSectionId() { return sectionId; }

    public void setAuctionId(String auctionId) { this.auctionId = auctionId; }
    public void setStartTime(Date startTime) { this.startTime = startTime; }
    public void setEndTime(Date endTime) { this.endTime = endTime; }
    public void setStatus(String status) { this.status = status; }
    public void setAvailableSlots(int availableSlots) { this.availableSlots = availableSlots; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }
    public void setSectionId(String sectionId) { this.sectionId = sectionId; }

    @Override
    public String toString() {
        return "Auction{" +
                "auctionId='" + auctionId + '\'' +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", status='" + status + '\'' +
                ", availableSlots=" + availableSlots +
                ", createdAt=" + createdAt +
                ", sectionId='" + sectionId + '\'' +
                '}';
    }
}
