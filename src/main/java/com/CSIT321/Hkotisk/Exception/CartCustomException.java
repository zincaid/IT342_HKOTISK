package com.CSIT321.Hkotisk.Exception;

public class CartCustomException extends RuntimeException {
    private static final long serialVersionUID = 2325949205764962693L;
    public CartCustomException(String message) {
        super(message);
    }
}
