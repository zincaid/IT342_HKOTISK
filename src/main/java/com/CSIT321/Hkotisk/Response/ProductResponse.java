package com.CSIT321.Hkotisk.Response;

import com.CSIT321.Hkotisk.Entity.ProductEntity;
import lombok.Data;

import java.util.List;

@Data
public class ProductResponse {
    private String status;
    private String message;
    private String AUTH_TOKEN;
    private List<ProductEntity> oblist;
}
