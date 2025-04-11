package com.CSIT321.Hkotisk.DTO;

import com.CSIT321.Hkotisk.Entity.User;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ReqRes implements Serializable {

    private int statusCode;
    private String error;
    private String message;
    private String token;
    private String refreshToken;
    private String expirationTime;
    private String email;
    private String username;
    private String password;
    private String role;
    private User student;
    private List<User> userList;
    private LocalDateTime timestamp;
    private String requestId;
    private Map<String, Object> additionalData;

}