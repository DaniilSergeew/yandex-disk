package com.example.yandexdisk.dao;

import com.example.yandexdisk.model.SystemItem;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.sql.*;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@Slf4j
public class SystemItemDao implements Dao<SystemItem> {
    @Override
    public Optional<SystemItem> findById(String id) {
        return Optional.empty();
    }

    @Override
    public List<SystemItem> getAllById() {
        return null;
    }

    @Override
    public void save(SystemItem systemItem) {
        log.info("Trying to connect to the database...");
        try (Connection connection = DriverManager
                .getConnection("jdbc:neo4j:http://localhost:7687", "neo4j", "pass");

             PreparedStatement stmt = connection
                     .prepareStatement(
                             "CREATE (s:SystemItem {type: \"$0\", UUID: \"$1\"})");) {
            log.info("The connection was successful");
            stmt.setString(0, systemItem.getSystemItemType().toString());
            stmt.setString(1, systemItem.getId().toString());

            log.info("Trying to execute the query...");
            stmt.executeUpdate();
            log.info("The query is successfully executed");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void update(SystemItem systemItem) {

    }

    @Override
    public void deleteById(SystemItem systemItem) {

    }
}
