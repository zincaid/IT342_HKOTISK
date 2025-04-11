package com.CSIT321.Hkotisk.Response;

import com.CSIT321.Hkotisk.Entity.CartEntity;
import lombok.Data;
import lombok.ToString;

import java.util.List;


@Data
@ToString
public class OrderResponse {
    private int orderId;
    private List<CartEntity> cartList;
}
