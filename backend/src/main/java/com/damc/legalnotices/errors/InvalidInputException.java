package com.damc.legalnotices.errors;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class InvalidInputException extends ResponseStatusException {
    private final java.util.Map<String, java.util.List<String>> errorMap = new java.util.HashMap<>();

    public InvalidInputException(String message, java.util.Map<String, java.util.List<String>> errorMap) {
        super(HttpStatus.BAD_REQUEST, message);
        this.errorMap.putAll(errorMap);
    }

    public InvalidInputException(String message) {
        super(HttpStatus.BAD_REQUEST, message);
    }

    public java.util.Map<String, java.util.List<String>> getErrorMap() {
        return errorMap;
    }
}