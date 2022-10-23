package com.example.yandexdisk.dao;

import com.example.yandexdisk.exception.EntityNotFoundException;

import java.util.List;

/**
 * Абстрактный класс с CRUD методами для графовой БД
 */
public abstract class GraphDao<T> {
// Todo: IllegalArgumentException - if id is null.
//    Todo: EntityNotFoundException это какая то подлива, надо хотя бы просто null возвращать
    public abstract int count();

    public abstract void deleteAll();

    public abstract void deleteAllByParentId(String id);

    public abstract boolean existsById(String id);

    public abstract void findAll();

    public abstract T findById(String id) throws EntityNotFoundException;

    abstract List<T> findAllById();

    public abstract void save(T t);

    public abstract void saveAll(List<T> t);

}
