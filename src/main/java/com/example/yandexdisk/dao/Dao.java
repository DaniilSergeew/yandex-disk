package com.example.yandexdisk.dao;

import java.util.List;
import java.util.Optional;

public abstract class Dao <T> {
    public abstract Optional<T> findById(String id);

    abstract List<T> getAllById();

    public abstract void save(T t);

    public abstract void update(T t);

    public abstract void deleteById(T t);
}
