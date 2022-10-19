package com.example.yandexdisk.exception;

import java.util.UUID;

public class SystemItemNotFoundException extends Exception {
    public SystemItemNotFoundException(UUID id) {
        super(String.format("SystemItem %s not found", id));
    }
}