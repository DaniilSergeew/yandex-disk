package com.example.yandexdisk.exception;

public class EntityNotFoundException extends Exception {
    /**
     *
     * @param id элемента в БД
     */
    public EntityNotFoundException(String id) {
        super(String.format("SystemItem %s not found", id));
    }
}