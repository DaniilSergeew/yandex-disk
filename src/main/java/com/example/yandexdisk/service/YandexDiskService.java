package com.example.yandexdisk.service;

import com.example.yandexdisk.dao.SystemItemDao;
import com.example.yandexdisk.dto.SystemItemImport;
import com.example.yandexdisk.dto.request.SystemItemImportRequest;
import com.example.yandexdisk.exception.ValidationException;
import com.example.yandexdisk.model.SystemItem;
import com.example.yandexdisk.model.SystemItemType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Класс, отвечающий за логику обработки запросов.
 * Работает с базой данных посредством объекта SystemItemDao
 */

@Slf4j
@RequiredArgsConstructor
@Service
public class YandexDiskService {
    private final SystemItemDao repository;

    public void handleSystemItemImportRequest(SystemItemImportRequest request) throws ValidationException {
        systemItemImportRequestIsValid(request);
        List<SystemItem> systemItems = new ArrayList<>();
        for (SystemItemImport systemItemImport : request.getItems()) {
            SystemItem systemItem = SystemItem.builder()
                    .id(systemItemImport.getId())
                    .url(systemItemImport.getUrl())
                    .parentId(systemItemImport.getParentId())
                    .date(request.getUpdateDate())
                    .systemItemType(systemItemImport.getType())
                    .size(systemItemImport.getSize())
                    .build();
            systemItems.add(systemItem);
        }
        log.info("Trying to save systemItems from date: {}", request.getUpdateDate());
        repository.saveAll(systemItems);
        log.info("Save systemItem from date: {} was successful", request.getUpdateDate());
    }

    public void handleSystemItemDeleteRequest(String id) {

    }

    public void handleSystemItemGetRequest(String id) {
        repository.findById(id);
    }

    /**
     * Проверяет валидность запроса на импорт
     * @param request объект запроса
     */
    private void systemItemImportRequestIsValid(SystemItemImportRequest request) throws ValidationException {
        Set<String> ids = new HashSet<>();
        for (SystemItemImport systemItemImport : request.getItems()) {
            // Проверка на: в одном запросе не может быть двух элементов с одинаковым id
            // Todo: добавить проверку на наличие id уже в БД
            if (ids.contains(systemItemImport.getId()))
                throw new ValidationException("ShopUnitImport ID shouldn't be repeated");
            else ids.add(systemItemImport.getId());
            // Проверка на: поле id не может быть равно null осуществляется аннотацией @NotNull в классе systemItemImport
            // Проверка на: родителем элемента может быть только папка
            if (systemItemImport.getParentId() != null) {
                // Родитель может быть не в запросе, а в базе?
                // Todo: сделать запрос в базу на поиск родителя и проверить тип элемента
                Optional<SystemItemImport> parentItem = request
                        .getItems()
                        .stream()
                        .filter(p -> p.getId().equals(systemItemImport.getParentId()))
                        .findFirst();
                if (parentItem.isPresent() && parentItem.get().getType() == SystemItemType.FILE) {
                    log.error("SystemItemImport ParentId: {} | The parent of a FILE or FOLDER can only be a FOLDER",
                            systemItemImport.getParentId());
                    throw new ValidationException("SystemItemImport Parent Id Exception");
                }
            }
            // Проверка на: поле url при импорте папки всегда должно быть равно null
            if (systemItemImport.getType() == SystemItemType.FOLDER) {
                if (systemItemImport.getUrl() != null) {
                    log.error("SystemItemImport with UUID: {} url for FOLDER should be NULL", systemItemImport.getId());
                    throw new ValidationException("SystemItemImport url Exception for FOLDER");
                }
            }
            // Проверка на: размер поля url при импорте файла всегда должен быть меньше либо равным 255
            if (systemItemImport.getType() == SystemItemType.FILE &&
                    systemItemImport.getUrl() != null && systemItemImport.getUrl().length() > 255) {
                log.error("SystemItemImport with UUID: {} url length should be less or equal than 255", systemItemImport.getId());
                throw new ValidationException("SystemItemImport url length Exception");
            }
            // Проверка на: поле size при импорте папки всегда должно быть равно null
            if (systemItemImport.getType() == SystemItemType.FOLDER &&
                    systemItemImport.getSize() != null) {
                log.error("SystemItemImport with UUID: {} size for FOLDER should be NULL", systemItemImport.getId());
                throw new ValidationException("SystemItemImport FOLDER size Exception");
            }
            // Проверка на: поле size для файлов всегда должно быть больше 0
            if (systemItemImport.getType() == SystemItemType.FILE &&
                    systemItemImport.getSize() <= 0) {
                log.error("SystemItemImport with UUID: {} size for Folder should be more than 0", systemItemImport.getId());
                throw new ValidationException("SystemItemImport FILE size Exception");
            }
        }
    }
}
