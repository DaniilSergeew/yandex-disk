package com.example.yandexdisk.dao;

import com.example.yandexdisk.exception.EntityNotFoundException;
import com.example.yandexdisk.model.SystemItem;
import com.example.yandexdisk.model.SystemItemType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Класс, реализующий crud операции с БД
 */

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
    public SystemItem findById(String id) throws EntityNotFoundException {
        String query = "MATCH (s:SystemItem) " +
                "WHERE s.id = $0 " +
                "RETURN s.type, s.id, s.date, s.url, s.size, s.parentId";
        log.info("Trying to connect to the database...");
        try (Connection con = getConnection();
             PreparedStatement stmt = con.prepareStatement(query)) {
            log.info("The connection was successful");
            stmt.setString(0, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    SystemItem systemItem = new SystemItem();
                    systemItem.setSystemItemType(SystemItemType.valueOf(rs.getString("s.type")));
                    systemItem.setId(rs.getString("s.id"));
                    systemItem.setDate(LocalDateTime.parse(rs.getString("s.date")));
                    systemItem.setUrl(rs.getString("s.url"));
                    systemItem.setSize(Integer.valueOf(rs.getString("s.size")));
                    systemItem.setParentId(rs.getString("s.parentId"));
                    return systemItem;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        throw new EntityNotFoundException(id);
    }

    @Override
    public List<SystemItem> getAllById() {
        return null;
    }

    // Todo: подумать как это все можно сломать

    /**
     * Сохраняет сущность в БД.
     * Создает двухстороннюю связь родитель-ребенок, если родитель тот есть в БД
     */
    @Override
    public void save(SystemItem systemItem) {
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
            SystemItem parent = null;
            try {
                parent = findById(systemItem.getParentId());
            } catch (EntityNotFoundException ignore) {
            }
            if (parent != null) {
                createRelationship(systemItem, parent);
            }
        }
    }

    /**
     * Сохраняет лист сущностей в БД.
     * Создает двухсторонние связи родитель-ребенок в БД, если это возможно.
     */
    @Override
    public void saveAll(List<SystemItem> systemItems) {
        // Создаем запрос к базе на сохранение и выполняем его
        String query = getSaveAllQuery(systemItems);
        log.info("Trying to connect to the database...");
        try (Connection con = getConnection();
             PreparedStatement stmt = con.prepareStatement(query)) {
            log.info("The connection was successful");
            log.info("Trying to execute the query to save systemItems");
            stmt.executeUpdate();
            log.info("The query is successfully executed");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        // Перебираем элементы и связываем их с родителями, если родители находятся в базе
        for (SystemItem systemItem : systemItems) {
            if (systemItem.getParentId() != null) {
                SystemItem parent = null;
                try {
                    parent = findById(systemItem.getParentId());
                } catch (EntityNotFoundException ignore) {
                }
                if (parent != null) {
                    createRelationship(systemItem, parent);
                }
            }
        }
    }

    /**
     * @return запрос на сохранение листа сущностей в БД без создания связей
     */
    private String getSaveAllQuery(List<SystemItem> systemItems) {
        StringBuilder query = new StringBuilder("CREATE ");
        for (int i = 0; i < systemItems.size(); i++) {
            SystemItem curr = systemItems.get(i);
            // add type
            query.append("(s:SystemItem {");
            query.append("type: ");
            query.append("\"").append(curr.getSystemItemType()).append("\"").append(", ");
            // add id
            query.append("id: ");
            query.append("\"").append(curr.getId()).append("\"").append(", ");
            // add url
            query.append("url: ");
            query.append("\"").append(curr.getUrl()).append("\"").append(", ");
            // add date
            query.append("date: ");
            query.append("\"").append(curr.getDate()).append("\"").append(", ");
            // add size
            query.append("size: ");
            query.append("\"").append(curr.getSize()).append("\"");
            query.append("})");
            if (i != systemItems.size() - 1) {
                query.append(", ");
            }
        }
        return query.toString();
    }

    @Override
    public void update(SystemItem systemItem) {

    }

    @Override
    public void deleteById(SystemItem systemItem) {

    }

    /**
     * Создает двухстороннюю связь родитель-ребенок в БД
     *
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
