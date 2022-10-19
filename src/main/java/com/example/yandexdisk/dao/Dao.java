package com.example.yandexdisk.dao;

import java.util.List;
import java.util.Optional;

public interface Dao <T> {
    Optional<T> findById(String id);

    List<T> getAllById();

    void save(T t);

    void update(T t);

    void deleteById(T t);
}
