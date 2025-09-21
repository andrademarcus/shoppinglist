package com.example.shoppinglist.exception;

public record ApiError(
        String timestamp,
        int status,
        String error,
        String message,
        String details) {

}
