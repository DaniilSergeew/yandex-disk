package com.example.yandexdisk.dao;

import java.util.List;
import java.util.Optional;

public abstract class Dao <T> {
    abstract Optional<T> findById(String id);

    abstract List<T> getAllById();

    abstract void save(T t);

    abstract void update(T t);

    abstract void deleteById(T t);
}
