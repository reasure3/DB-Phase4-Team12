package com.team12.auction.model.entity;

public class Basket {
    private String basketId;
    private int studentId;

    public Basket() {}

    public Basket(String basketId, int studentId) {
        this.basketId = basketId;
        this.studentId = studentId;
    }

    public String getBasketId() { return basketId; }
    public int getStudentId() { return studentId; }

    public void setBasketId(String basketId) { this.basketId = basketId; }
    public void setStudentId(int studentId) { this.studentId = studentId; }

    @Override
    public String toString() {
        return "Basket{" +
                "basketId='" + basketId + '\'' +
                ", studentId=" + studentId +
                '}';
    }
}
