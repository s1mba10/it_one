package com.example.kapital;

import java.util.Date;

public class Operation {
    private double sum;
    private String category;
    private Date date;
    private String type;  // "income" or "expense"

    public Operation() {
        this.date = new Date();
    }

    public double getSum() {
        return sum;
    }

    public void setSum(double sum) {
        this.sum = sum;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
