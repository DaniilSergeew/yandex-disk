package com.example.yandexdisk.dao;

import com.example.yandexdisk.model.SystemItem;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class SystemItemDao implements Dao<SystemItem> {
    @Override
    public Optional<SystemItem> findById(UUID id) {
        return Optional.empty();
    }

    @Override
    public List<SystemItem> getAllById() {
        return null;
    }

    @Override
    public void save(SystemItem systemItem) {

    }

    @Override
    public void update(SystemItem systemItem) {

    }

    @Override
    public void deleteById(SystemItem systemItem) {

    }
}
