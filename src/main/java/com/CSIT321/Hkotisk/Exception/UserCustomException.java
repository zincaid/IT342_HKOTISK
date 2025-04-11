package com.CSIT321.Hkotisk.Exception;

public class UserCustomException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public UserCustomException(String message) {
        super(message);
    }
}