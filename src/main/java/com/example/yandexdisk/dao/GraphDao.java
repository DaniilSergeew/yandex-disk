package com.example.yandexdisk.dao;

import com.example.yandexdisk.dto.SystemItemExport;
import com.example.yandexdisk.model.SystemItem;

import java.util.List;
import java.util.Optional;

/**
 * Абстрактный класс с CRUD методами для графовой БД
 */
public abstract class GraphDao<T> {
    public abstract int count();

    public abstract void deleteAll();

    public abstract void deleteAllByParentId(String id) throws IllegalArgumentException;

    public abstract boolean existsById(String id) throws IllegalArgumentException;

    public abstract void findAll();

    public abstract Optional<SystemItem> findById(String id) throws IllegalArgumentException;

    public abstract Optional<SystemItemExport> findAllById(String id) throws IllegalArgumentException;

    public abstract void save(T t) throws IllegalArgumentException;

    public abstract void saveAll(List<T> t) throws IllegalArgumentException;

}
