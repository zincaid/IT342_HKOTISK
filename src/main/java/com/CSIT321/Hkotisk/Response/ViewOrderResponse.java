package com.CSIT321.Hkotisk.Response;

import lombok.Data;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Data
@ToString
public class ViewOrderResponse {
    private String status;
    private String message;
    private String AUTH_TOKEN;
    private List<Order> orderlist = new ArrayList<>();
}
