package com.example.yandexdisk.dao;

import com.example.yandexdisk.model.SystemItem;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.sql.*;
import java.util.List;
import java.util.Optional;

@Slf4j
@Component
public class SystemItemDao extends Dao<SystemItem> {

    private Connection getConnection() throws SQLException {
        String path = "jdbc:neo4j:bolt://localhost:7687";
        String user = "neo4j";
        String password = "pass";
        return DriverManager.getConnection(path, user, password);
    }

    @Override
    public Optional<SystemItem> findById(String id) {
        String query = "MATCH (s:SystemItem) WHERE s.id = $0 RETURN s";
        log.info("Trying to connect to the database...");
        try (Connection con = getConnection();
             PreparedStatement stmt = con.prepareStatement(query)) {
            log.info("The connection was successful");
            stmt.setString(0, id);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    // Todo: собрать объект и подумать над тем, как вернуть пустой Optional, если ничего не нашлось
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return Optional.empty();
    }

    @Override
    public List<SystemItem> getAllById() {
        return null;
    }

    @Override
    public void save(SystemItem systemItem) {
        // CREATE CONSTRAINT UUID_UNIQUE FOR (s:SystemItem) REQUIRE s.UUID IS UNIQUE
        String query = "CREATE (s:SystemItem {type: $0, id: $1, url: $2, date: $3, size: $4})";
        log.info("Trying to connect to the database...");
        try (Connection con = getConnection();
             PreparedStatement stmt = con.prepareStatement(query)) {
            log.info("The connection was successful");

            stmt.setString(0, systemItem.getSystemItemType().toString());
            stmt.setString(1, systemItem.getId());
            stmt.setString(2, systemItem.getUrl());
            stmt.setString(3, systemItem.getDate().toString());
            stmt.setString(4, systemItem.getSize().toString());
            // Todo: написать более подробный лог
            log.info("Trying to execute the query...");
            stmt.executeUpdate();
            log.info("The query is successfully executed");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        if (systemItem.getParentId() != null) {
            // Если родитель уже в базе
            Optional<SystemItem> parent = findById(systemItem.getParentId());
            if (parent.isPresent()) {
                query = "MATCH (child:SystemItem), (parent:SystemItem)  \n" +
                        "WHERE child.UUID = $0 AND parent.UUID = $1" +
                        "CREATE (child)-[:child]->(parent),\n" +
                        "(parent)-[:parent]->(child)\n" +
                        "RETURN child,parent";

                log.info("Trying to connect to the database...");
                try (Connection con = getConnection();
                     PreparedStatement stmt = con.prepareStatement(query)) {
                    log.info("The connection was successful");

                    stmt.setString(0, systemItem.getId());
                    stmt.setString(1, parent.get().getId());

                    // Todo: написать более подробный лог
                    log.info("Trying to execute the query...");
                    stmt.executeUpdate();
                    log.info("The query is successfully executed");
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
            // Если родитель будет дальше в запросе, то ребенок будет в бд и выполнится код сверху
        }
    }


    @Override
    public void update(SystemItem systemItem) {

    }

    @Override
    public void deleteById(SystemItem systemItem) {

    }
}
