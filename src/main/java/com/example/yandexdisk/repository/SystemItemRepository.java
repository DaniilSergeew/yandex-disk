package com.example.yandexdisk.repository;

import com.example.yandexdisk.dto.SystemItemExport;
import com.example.yandexdisk.exception.EntityNotFoundException;
import com.example.yandexdisk.model.SystemItem;
import com.example.yandexdisk.model.SystemItemType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Класс, реализующий CRUD операции с графовой БД
 */

@Slf4j
@Component
public class SystemItemRepository extends GraphRepository<SystemItem> {

    private Connection getConnection() throws SQLException {
        String path = "jdbc:neo4j:bolt://localhost:7687";
        String user = "neo4j";
        String password = "pass";
        return DriverManager.getConnection(path, user, password);
    }

    /**
     * @return количество экземпляров SystemItem в БД
     */
    @Override
    public int count() {
        String query = """
                MATCH (n:SystemItem)
                RETURN count(*) as counter""";
        log.info("Trying to connect to the database...");
        try (Connection con = getConnection();
             PreparedStatement stmt = con.prepareStatement(query)) {
            log.info("Trying to execute the query to find count of nodes...");
            stmt.executeQuery();
            log.info("The query is successfully executed");
            try (ResultSet rs = stmt.executeQuery()) {
                rs.next();
                return Integer.parseInt(rs.getString("counter"));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Удаляет все элементы из БД
     */
    @Override
    public void deleteAll() {
        String query = "MATCH (n) DETACH DELETE n";
        log.info("Trying to connect to the database...");
        try (Connection con = getConnection();
             PreparedStatement stmt = con.prepareStatement(query)) {
            log.info("Trying to execute the query to delete all nodes...");
            stmt.executeUpdate();
            log.info("The query is successfully executed");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Удаляет элемент в БД по его id, включая все дочерние элементы
     * @param id рута в БД
     * @throws IllegalArgumentException если id равен null.
     */
    @Override
    public void deleteAllByParentId(String id) throws IllegalArgumentException, EntityNotFoundException {
        // Я не знаю как каскадно удалить элементы и поэтому сделаю это итеративно
        if (id == null) {
            throw new IllegalArgumentException();
        }
        SystemItem root = findById(id).orElseThrow(() -> new EntityNotFoundException(id));
        List<SystemItem> elementsForDeleting = findAllDownItems(root);
        for (int i = elementsForDeleting.size() - 1; i >= 0; i--) {
            deleteByParentId(elementsForDeleting.get(i).getId());
        }
    }

    /**
     * Удаляет элемент по его id и все связи вокруг него.
     * @param id элемента в БД.
     * @throws IllegalArgumentException если id равен null.
     */
    private void deleteByParentId(String id) throws IllegalArgumentException {
        if (id == null) {
            throw new IllegalArgumentException();
        }
        String query = """
                MATCH (c:SystemItem)
                WHERE c.id = $0
                DETACH DELETE c""";
        log.info("Trying to connect to the database...");
        try (Connection con = getConnection();
             PreparedStatement stmt = con.prepareStatement(query)) {
            stmt.setString(0, id);
            log.info("Trying to execute the query to delete node with id: {}", id);
            stmt.executeUpdate();
            log.info("The query is successfully executed");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @return истину, в случае, если элемент с указанным id имеется в БД.
     * @param id элемента в БД.
     * @throws IllegalArgumentException если id равен null.
     */
    @Override
    public boolean existsById(String id) throws IllegalArgumentException {
        if (id == null) {
            throw new IllegalArgumentException();
        }
        return findById(id).isPresent();
    }

    @Override
    public void findAll() {

    }

    /**
     * @return сущность из БД по ее id
     * @param id элемента в БД
     * @throws IllegalArgumentException если id равен null
     */
    @Override
    public Optional<SystemItem> findById(String id) throws IllegalArgumentException {
        if (id == null) {
            throw new IllegalArgumentException();
        }
        String query = """
                MATCH (s:SystemItem)
                WHERE s.id = $0
                RETURN s.id, s.url, s.parentId, s.date, s.type, s.size""";
        log.info("Trying to connect to the database...");
        try (Connection con = getConnection();
             PreparedStatement stmt = con.prepareStatement(query)) {
            log.info("The connection was successful");
            log.info("Trying to execute the query to find SystemItem with id {}...", id);
            stmt.setString(0, id);
            log.info("The query is successfully executed");
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    SystemItem systemItem = new SystemItem();
                    systemItem.setId(rs.getString("s.id"));
                    systemItem.setUrl(rs.getString("s.url"));
                    systemItem.setParentId(rs.getString("s.parentId"));
                    systemItem.setDate(LocalDateTime.parse(rs.getString("s.date")));
                    systemItem.setType(SystemItemType.valueOf(rs.getString("s.type")));
                    try {
                        systemItem.setSize(Integer.valueOf(rs.getString("s.size")));
                    } catch (NumberFormatException e) {
                        systemItem.setSize(null);
                    }
                    log.info("Entity is successfully founded");
                    return Optional.of(systemItem);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        log.info("Entity not found");
        return Optional.empty();
    }

    /**
     *
     * @param id элемента в БД
     * @return всю иерархию элементов в БД начиная с элемента рута
     * @throws IllegalArgumentException если id равен null
     */
    @Override
    public Optional<SystemItemExport> findAllById(String id) throws IllegalArgumentException {
        if (id == null) {
            throw new IllegalArgumentException();
        }
        // Находим корень и если его не существует возвращаем Optional.empty()
        Optional<SystemItem> optionalRoot = findById(id);
        if (optionalRoot.isEmpty()) {
            return Optional.empty();
        }
        // создаем корень SystemItemExport
        SystemItem rootSystemItem = optionalRoot.get();
        SystemItemExport globalRoot = SystemItemExport.builder()
                .id(id)
                .url(rootSystemItem.getUrl())
                .parentId(rootSystemItem.getParentId())
                .date(rootSystemItem.getDate())
                .type(rootSystemItem.getType())
                .size(rootSystemItem.getSize())
                .build();
        // если рутом является файл, то просто возвращаем его
        if (globalRoot.getType() == SystemItemType.FILE) {
            return Optional.of(globalRoot);
        }
        // Создаем иерархию...
        // Корень на текущей итерации
        List<SystemItemExport> up = new CopyOnWriteArrayList<>();
        up.add(globalRoot);
        // Дети корня на текщей итерации
        List<SystemItemExport> middle = new CopyOnWriteArrayList<>(findAllExportChildrenById(id));
        globalRoot.setChildren(new CopyOnWriteArrayList<>());
        // Внуки на текущей итерации
        List<SystemItemExport> down = new CopyOnWriteArrayList<>();
        // Цикл, отвечающйи за проход по "этажам"
        while (true) {
            // Перебор корней
            for (SystemItemExport upItem : up) {
                // Перебор детей каждого корня
                for (SystemItemExport middleItem : middle) {
                    // Сохраняем всех внуков в одном общем массиве
                    down.addAll(findAllExportChildrenById(middleItem.getId()));
                    // Если мэтчатся родитель и ребенок, то добавляем ребенка родителю
                    if (middleItem.getParentId().equals(upItem.getId())) {
                        upItem.getChildren().add(middleItem);
                    }
                }
            }
            // Условие выхода: отсутствие детей у среднего уровня, которого уже связали с родителями
            if (down.isEmpty()) {
                return Optional.of(globalRoot);
            }
            // На следующей итерации к текущему среднему уровню добавятся текущие внуки, для этого сдвигаем уровни вниз
            up = new CopyOnWriteArrayList<>(middle);
            middle = new CopyOnWriteArrayList<>(down);
            down.clear();
        }
    }

    /**
     * Сохраняет сущность в БД.
     * Создает двухстороннюю связь родитель-ребенок, если родитель тот есть в БД
     * @throws IllegalArgumentException если systemItem равен null
     */
    @Override
    public void save(SystemItem systemItem) throws IllegalArgumentException {
        if (systemItem == null) {
            throw new IllegalArgumentException();
        }
        String query = "CREATE (s:SystemItem {id: $0, url: $1, parentId: $2, date: $3, type: $4, size: $5})";
        log.info("Trying to connect to the database...");
        try (Connection con = getConnection();
             PreparedStatement stmt = con.prepareStatement(query)) {
            log.info("The connection was successful");

            stmt.setString(0, systemItem.getId());
            stmt.setString(1, systemItem.getUrl());
            stmt.setString(2, systemItem.getParentId());
            stmt.setString(3, systemItem.getDate().toString());
            stmt.setString(4, systemItem.getType().toString());
            stmt.setString(5, systemItem.getSize().toString());

            log.info("Trying to execute the query to save {}...", systemItem.getId());
            stmt.executeUpdate();
            log.info("The query is successfully executed");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        // Если родитель уже в базе
        if (systemItem.getParentId() != null) {
            Optional<SystemItem> parent = findById(systemItem.getParentId());
            parent.ifPresent(item -> createRelationship(systemItem, item));
        }
    }

    /**
     * Сохраняет лист сущностей в БД.
     * Создает двухсторонние связи родитель-ребенок в БД, если это возможно.
     * @throws IllegalArgumentException если systemItems равен null
     */
    @Override
    public void saveAll(List<SystemItem> systemItems) throws IllegalArgumentException {
        if (systemItems == null) {
            throw new IllegalArgumentException();
        }
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
                Optional<SystemItem> parent = findById(systemItem.getParentId());
                parent.ifPresent(item -> createRelationship(systemItem, item));
            }
        }
        processSumOfChild();
    }

    /**
     * @return запрос на сохранение листа сущностей в БД без создания связей
     * @throws IllegalArgumentException если systemItems равен null
     */
    private String getSaveAllQuery(List<SystemItem> systemItems) throws IllegalArgumentException {
        if (systemItems == null) {
            throw new IllegalArgumentException();
        }
        StringBuilder query = new StringBuilder("CREATE ");
        for (int i = 0; i < systemItems.size(); i++) {
            SystemItem curr = systemItems.get(i);
            query.append("(s");
            query.append(i);
            query.append(":SystemItem {");
            // add id
            query.append("id: ");
            query.append("\"").append(curr.getId()).append("\"").append(", ");
            // add url
            query.append("url: ");
            query.append("\"").append(curr.getUrl()).append("\"").append(", ");
            // add parentId
            query.append("parentId: ");
            query.append("\"").append(curr.getParentId()).append("\"").append(", ");
            // add date
            query.append("date: ");
            query.append("\"").append(curr.getDate()).append("\"").append(", ");
            // add type
            query.append("type: ");
            query.append("\"").append(curr.getType()).append("\"").append(", ");
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

    /**
     * Создает двухстороннюю связь родитель-ребенок в БД
     *
     * @param child  ребенок, уже имеющийся в БД
     * @param parent родитель, уже имеющийся в БД
     * @throws IllegalArgumentException если хотя бы один из параметров равен null
     */
    private void createRelationship(SystemItem child, SystemItem parent) throws IllegalArgumentException {
        if (child == null || parent == null) {
            throw new IllegalArgumentException();
        }
        String query = """
                MATCH (child:SystemItem), (parent:SystemItem)
                WHERE child.id = $0 AND parent.id = $1
                CREATE (child)-[:child]->(parent),
                (parent)-[:parent]->(child)""";

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

    /**
     * @throws IllegalArgumentException если id равен null
     */
    private List<SystemItemExport> findAllExportChildrenById(String id) throws IllegalArgumentException {
        if (id == null) {
            throw new IllegalArgumentException();
        }
        List<SystemItemExport> allChildren = new ArrayList<>();
        String query = """
                MATCH (s:SystemItem {id: $0})-[:parent]->(c:SystemItem)
                RETURN c.id, c.url, c.parentId, c.date, c.type, c.size""";
        try (Connection con = getConnection();
             PreparedStatement stmt = con.prepareStatement(query)) {
            stmt.setString(0, id);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    SystemItemExport current = new SystemItemExport();
                    current.setId(rs.getString("c.id"));
                    current.setUrl(rs.getString("c.url"));
                    current.setParentId(rs.getString("c.parentId"));
                    current.setDate(LocalDateTime.parse(rs.getString("c.date")));
                    current.setType(SystemItemType.valueOf(rs.getString("c.type")));
                    try {
                        current.setSize(Integer.valueOf(rs.getString("c.size")));
                    } catch (NumberFormatException e) {
                        current.setSize(null);
                    }
                    // Для файлов childer = null
                    if (current.getType() == SystemItemType.FILE) {
                        current.setChildren(null);
                    } else {
                        current.setChildren(new ArrayList<>());
                    }
                    allChildren.add(current);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return allChildren;
    }

    private List<SystemItem> findAllChildrenById(String id) {
        List<SystemItem> allChildren = new ArrayList<>();
        String query = """
                MATCH (s:SystemItem {id: $0})-[:parent]->(c:SystemItem)
                RETURN c.id, c.url, c.parentId, c.date, c.type, c.size""";
        try (Connection con = getConnection();
             PreparedStatement stmt = con.prepareStatement(query)) {
            stmt.setString(0, id);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    SystemItem current = new SystemItem();
                    current.setId(rs.getString("c.id"));
                    current.setUrl(rs.getString("c.url"));
                    current.setParentId(rs.getString("c.parentId"));
                    current.setDate(LocalDateTime.parse(rs.getString("c.date")));
                    current.setType(SystemItemType.valueOf(rs.getString("c.type")));
                    try {
                        current.setSize(Integer.valueOf(rs.getString("c.size")));
                    } catch (NumberFormatException e) {
                        current.setSize(null);
                    }
                    allChildren.add(current);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return allChildren;
    }

    /**
     * Вычисляет суммарный размер всех элементов каждой папки и сохраняет изменения в БД.
     * Необходимо вызывать после DDL и DML операций с БД для сохранения консистентности данных.
     */
    private void processSumOfChild() {
        // Запросить все узлы, которые являются рутами
        // Потом... пиздец нахуй блин
    }

    /**
     *
     * @return список элементов которые находятся ниже по иерархии
     * в БД, включая root
     */
    private List<SystemItem> findAllDownItems(SystemItem root) {
        List<SystemItem> allDownItems = new ArrayList<>();
        allDownItems.add(root);
        allDownItems.addAll(findAllChildrenById(root.getId()));
        List<SystemItem> up = new CopyOnWriteArrayList<>();
        up.add(root);
        // Дети корня на текщей итерации
        List<SystemItem> middle = new CopyOnWriteArrayList<>(findAllChildrenById(root.getId()));
        // Внуки на текущей итерации
        List<SystemItem> down = new CopyOnWriteArrayList<>();
        // Цикл, отвечающйи за проход по "этажам"
        while (true) {
            boolean listIsNotChanged = true;
            // Перебор корней
            for (SystemItem upItem : up) {
                // Перебор детей каждого корня
                for (SystemItem middleItem : middle) {
                    // Сохраняем всех внуков в одном общем массиве
                    if (down.addAll(findAllChildrenById(middleItem.getId()))) {
                        listIsNotChanged = false;
                    }
                }
            }
            // Условие выхода: отсутствие детей у среднего уровня
            if (listIsNotChanged) {
                break;
            }
            up = new CopyOnWriteArrayList<>(middle);
            middle = new CopyOnWriteArrayList<>(down);
            allDownItems.addAll(down);
            down.clear();
        }
        return allDownItems;
    }

}
