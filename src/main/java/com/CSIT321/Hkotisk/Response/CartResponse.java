package com.CSIT321.Hkotisk.Response;

import com.CSIT321.Hkotisk.Entity.CartEntity;
import lombok.Data;

import java.util.List;

@Data
public class CartResponse {
    private String status;
    private String message;
    private String AUTH_TOKEN;
    private List<CartEntity> oblist;

}
