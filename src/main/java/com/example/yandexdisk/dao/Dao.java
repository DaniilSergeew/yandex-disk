package com.example.yandexdisk.dao;

import com.example.yandexdisk.exception.EntityNotFoundException;

import java.util.List;

/**
 * Абстрактный класс с CRUD методами
 */
public abstract class Dao<T> {
    public abstract T findById(String id) throws EntityNotFoundException;

    abstract List<T> getAllById();

    public abstract void save(T t);

    public abstract void saveAll(List<T> t);

    public abstract void update(T t);

    public abstract void deleteById(T t);
}
