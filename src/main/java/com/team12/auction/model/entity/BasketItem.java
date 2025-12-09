package com.team12.auction.model.entity;

import java.sql.Date;

public class BasketItem {
	private Date registrationTime;
	private String status;
	private Date processedTime;
	private String reason;
	private String basketId;
	private String sectionId;

	public BasketItem() {
	}

	public BasketItem(Date registrationTime, String status, Date processedTime, String reason, String basketId,
			String sectionId) {
		this.registrationTime = registrationTime;
		this.status = status;
		this.processedTime = processedTime;
		this.reason = reason;
		this.basketId = basketId;
		this.sectionId = sectionId;
	}

	public Date getRegistrationTime() {
		return registrationTime;
	}

	public String getStatus() {
		return status;
	}

	public Date getProcessedTime() {
		return processedTime;
	}

	public String getReason() {
		return reason;
	}

	public String getBasketId() {
		return basketId;
	}

	public String getSectionId() {
		return sectionId;
	}

	public void setRegistrationTime(Date registrationTime) {
		this.registrationTime = registrationTime;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public void setProcessedTime(Date processedTime) {
		this.processedTime = processedTime;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public void setBasketId(String basketId) {
		this.basketId = basketId;
	}

	public void setSectionId(String sectionId) {
		this.sectionId = sectionId;
	}

	@Override
	public String toString() {
		return "BasketItem{" + "registrationTime=" + registrationTime + ", status='" + status + '\''
				+ ", processedTime=" + processedTime + ", reason='" + reason + '\'' + ", basketId='" + basketId + '\''
				+ ", sectionId='" + sectionId + '\'' + '}';
	}
}
