package com.CSIT321.Hkotisk.DTO;

import java.util.Date;
import java.util.List;

import com.CSIT321.Hkotisk.Entity.CartEntity;

import lombok.Data;

@Data
public class OrderDetailsDTO {
    private int orderId;
    private String email;
    private String orderStatus;
    private Date orderDate;
    private double totalCost;
    private List<CartEntity> items;
}
