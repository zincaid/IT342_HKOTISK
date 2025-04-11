package com.CSIT321.Hkotisk.Response;

import lombok.Data;

import java.io.Serializable;
import java.util.HashMap;

@Data
public class Response implements Serializable {
    private static final long serialVersionUID = 1928909901056236719L;
    private String status;
    private String message;
    private String AUTH_TOKEN;
    private HashMap<String, String> map;
}
