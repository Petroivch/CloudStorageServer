package com.example.cloud.exception;

import org.springframework.security.core.AuthenticationException;

public class StorageException extends AuthenticationException {
    public StorageException(String message) {
        super(message);
    }
}