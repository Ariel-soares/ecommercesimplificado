package com.arielsoares.ecommercesimplificado.exception;

import java.util.HashMap;
import java.util.Map;

public class ValidationErrorDetails {
    private String message;
    private Map<String, String> errors = new HashMap<>();

    public ValidationErrorDetails(String message) {
        this.message = message;
    }

    public void addError(String field, String errorMessage) {
        errors.put(field, errorMessage);
    }

    // Getters and Setters
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Map<String, String> getErrors() {
        return errors;
    }

    public void setErrors(Map<String, String> errors) {
        this.errors = errors;
    }
}