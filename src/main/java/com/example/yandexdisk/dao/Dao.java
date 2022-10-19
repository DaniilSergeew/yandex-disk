package com.example.yandexdisk.dao;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface Dao <T> {
    Optional<T> getById(UUID id);

    List<T> getAllById();

    void save(T t);

    void update(T t);

    void deleteById(T t);
}
