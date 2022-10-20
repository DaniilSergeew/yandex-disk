package com.example.yandexdisk.dao;

import com.example.yandexdisk.model.SystemItem;
import com.example.yandexdisk.model.SystemItemType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * Класс, реализующий crud операции с БД
 */

@Slf4j
@Component
public class SystemItemDao extends Dao<SystemItem> {
    // Todo: выдохни, погуляй и хорошо подумай как все сделать чисто, аккуратно и чтобы ахуенно работало
    // Сделать метод saveAll(List<SystemItem>), который будет формировать один запрос на сохранение и  на создание связей
    // Соответственно сохранять не поштучно а пачкой сразу
    // Итого: получили транзакционность импорта!!
    private Connection getConnection() throws SQLException {
        String path = "jdbc:neo4j:bolt://localhost:7687";
        String user = "neo4j";
        String password = "pass";
        return DriverManager.getConnection(path, user, password);
    }

    @Override
    public Optional<SystemItem> findById(String id) {
        // Todo: возвращать не обьект а список полей экземпляра
        String query = "MATCH (s:SystemItem) WHERE s.id = $0 RETURN s";
        log.info("Trying to connect to the database...");
        try (Connection con = getConnection();
             PreparedStatement stmt = con.prepareStatement(query)) {
            log.info("The connection was successful");
            stmt.setString(0, id);
            try (ResultSet rs = stmt.executeQuery()) {
                // Todo: выбросить исключение если ничего не нашли
                SystemItem systemItem = new SystemItem();
                    systemItem.setSystemItemType(SystemItemType.valueOf(rs.getString("s.type")));
                    systemItem.setId(rs.getString("s.id"));
                    systemItem.setDate(LocalDateTime.parse(rs.getString("s.date")));
                    systemItem.setUrl(rs.getString("s.url"));
                    systemItem.setSize(Integer.valueOf(rs.getString("s.size")));
                    return Optional.of(systemItem);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<SystemItem> getAllById() {
        return null;
    }

    // Todo: подумать как это все можно сломать
    /**
     * Сохраняет сущность в БД.
     * Создает двухстороннюю связь родитель-ребенок, если тот есть в БД
     */
    @Override
    public void save(SystemItem systemItem) {
        // Todo: подумать над транзакционностью всего этого дела
        // Решение: сохранять не множеством запросов, а в один запрос всю пачку
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

            log.info("Trying to execute the query to save {}...", systemItem.getId());
            stmt.executeUpdate();
            log.info("The query is successfully executed");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        // Если родитель уже в базе
        if (systemItem.getParentId() != null) {
            Optional<SystemItem> optionalParent = findById(systemItem.getParentId());
            if (optionalParent.isPresent()) {
                SystemItem parent = optionalParent.get();
                createRelationship(systemItem, parent);
            }
        }
    }

    @Override
    public void saveAll(Collection<SystemItem> t) {

    }


    @Override
    public void update(SystemItem systemItem) {

    }

    @Override
    public void deleteById(SystemItem systemItem) {

    }

    /**
     * Создает двухстороннюю связь родитель-ребенок в БД
     * @param child  ребенок, уже имеющийся в БД
     * @param parent родитель, уже имеющийся в БД
     */
    private void createRelationship(SystemItem child, SystemItem parent) {
        String query = "MATCH (child:SystemItem), (parent:SystemItem)  \n" +
                "WHERE child.UUID = $0 AND parent.UUID = $1" +
                "CREATE (child)-[:child]->(parent),\n" +
                "(parent)-[:parent]->(child)\n" +
                "RETURN child,parent";

        log.info("Trying to connect to the database...");
        try (Connection con = getConnection();
             PreparedStatement stmt = con.prepareStatement(query)) {
            log.info("The connection was successful");

            stmt.setString(0, child.getId());
            stmt.setString(1, parent.getId());

            log.info("Trying to execute the query to create relationship between {} and {}...",
                    child.getId(), parent.getId());
            stmt.executeUpdate();
            log.info("The query is successfully executed");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
