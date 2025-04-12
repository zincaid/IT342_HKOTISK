package com.example.hkotisk_mobile.model;

public class OrderAgainItem {
    private String name;
    private int imageResourceId;
    private int available;
    private int sold;
    private double price;

    public OrderAgainItem(String name, int imageResourceId, int available, int sold, double price) {
        this.name = name;
        this.imageResourceId = imageResourceId;
        this.available = available;
        this.sold = sold;
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getImageResourceId() {
        return imageResourceId;
    }

    public void setImageResourceId(int imageResourceId) {
        this.imageResourceId = imageResourceId;
    }

    public int getAvailable() {
        return available;
    }

    public void setAvailable(int available) {
        this.available = available;
    }

    public int getSold() {
        return sold;
    }

    public void setSold(int sold) {
        this.sold = sold;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
} 