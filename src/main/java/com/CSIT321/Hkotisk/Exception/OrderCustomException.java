package com.CSIT321.Hkotisk.Exception;

public class OrderCustomException extends RuntimeException {
    private static final long serialVersionUID = -3006580967312049506L;
    public OrderCustomException(String message) {
        super(message);
    }
}
