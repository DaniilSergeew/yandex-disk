package com.example.yandexdisk.exception;

public class EntityNotFoundException extends Exception {
    public EntityNotFoundException(String id) {
        super(String.format("SystemItem %s not found", id));
    }
}