package com.CSIT321.Hkotisk.Response;

import com.CSIT321.Hkotisk.Entity.CartEntity;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class Order {
    private int orderId;
    private String orderBy;
    private String orderStatus;
    private List<CartEntity> products = new ArrayList<>();
}
