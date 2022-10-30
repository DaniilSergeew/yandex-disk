package com.example.yandexdisk.service;

import com.example.yandexdisk.repository.SystemItemRepository;
import com.example.yandexdisk.dto.SystemItemExport;
import com.example.yandexdisk.dto.SystemItemImport;
import com.example.yandexdisk.dto.request.SystemItemImportRequest;
import com.example.yandexdisk.exception.EntityNotFoundException;
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
// Todo: добавить в сообщения об исключениях id
@Slf4j
@RequiredArgsConstructor
@Service
public class YandexDiskService {
    private final SystemItemRepository repository;

    /**
     * path: /imports
     */
    public void handleSystemItemImportRequest(SystemItemImportRequest request) throws ValidationException {
        systemItemImportRequestIsValid(request);
        List<SystemItem> systemItems = new ArrayList<>();
        for (SystemItemImport systemItemImport : request.getItems()) {
            SystemItem systemItem = SystemItem.builder()
                    .id(systemItemImport.getId())
                    .url(systemItemImport.getUrl())
                    .parentId(systemItemImport.getParentId())
                    .date(request.getUpdateDate())
                    .type(systemItemImport.getType())
                    .size(systemItemImport.getSize())
                    .build();
            systemItems.add(systemItem);
        }
        repository.saveAll(systemItems);
    }

    /**
     * path: /delete/{id}
     */
    public void handleSystemItemsDeleteRequest(String id) throws EntityNotFoundException {
        repository.deleteAllByParentId(id);
    }

    /**
     * path: /nodes/{id}
     */
    public SystemItemExport handleSystemItemGetRequest(String id) throws EntityNotFoundException {
        Optional<SystemItemExport> response = repository.findAllById(id);
        if (response.isEmpty()) {
            log.info("Getting SystemItems by id: {} was failed", id);
            throw new EntityNotFoundException(id);
        }
        return response.get();
    }

    /**
     * Проверяет валидность запроса на импорт
     *
     * @param request объект запроса
     */
    private void systemItemImportRequestIsValid(SystemItemImportRequest request) throws ValidationException {
        checkUniqueId(request);
        checkTypeOfParent(request);
        checkUrlOfFolder(request);
        checkUrlSize(request);
        checkFolderSize(request);
        checkFileSize(request);
        checkIdIsEmpty(request);
        checkIdEuqalsParentId(request);
    }

    /**
     * Проверка на: в одном запросе не может быть двух элементов с одинаковым id
     */
    private void checkUniqueId(SystemItemImportRequest request) throws ValidationException {
        Set<String> ids = new HashSet<>();
        for (SystemItemImport systemItemImport : request.getItems()) {
            if (ids.contains(systemItemImport.getId())) {
                log.error("SystemItemImport Id {} is repeated", systemItemImport.getId());
                throw new ValidationException("SystemItemImport ID shouldn't be repeated");
            } else ids.add(systemItemImport.getId());
        }
    }

    private void checkIdEuqalsParentId(SystemItemImportRequest request) throws ValidationException {
        for (SystemItemImport systemItemImport : request.getItems()) {
            if (systemItemImport.getId().equals(systemItemImport.getParentId())) {
                log.error("SystemItemImport with {} | euqals SystemItemImport parentId", systemItemImport.getId());
                throw new ValidationException("SystemItemImport ID shouldn't be euqals SystemItemImport parentId");
            }
        }
    }

    /**
     * Проверка на: id элемента не может быть пустой строкой
     */
    private void checkIdIsEmpty(SystemItemImportRequest request) throws ValidationException {
        for (SystemItemImport systemItemImport : request.getItems()) {
            if (systemItemImport.getId().isEmpty()) {
                log.error("SystemItemImport id is empty");
                throw new ValidationException("SystemItemImport ID shouldn't be empty");
            }
        }
    }

    /**
     * Проверка на: родителем элемента может быть только папка
     */
    private void checkTypeOfParent(SystemItemImportRequest request) throws ValidationException {
        for (SystemItemImport systemItemImport : request.getItems()) {
            // Поиск по запросу
            if (systemItemImport.getParentId() != null) {
                Optional<SystemItemImport> optionalParent = request
                        .getItems()
                        .stream()
                        .filter(p -> p.getId().equals(systemItemImport.getParentId()))
                        .findFirst();
                // Поиск по БД
                if (optionalParent.isPresent() && optionalParent.get().getType() == SystemItemType.FILE) {
                    log.error("SystemItemImport with ParentId: {} | have a file parent",
                            systemItemImport.getParentId());
                    throw new ValidationException("SystemItemImport with ParentId: {} | The parent of a FILE or FOLDER can only be a FOLDER");
                }

                Optional<SystemItem> parentFromRepository = repository.findById(systemItemImport.getParentId());
                if (parentFromRepository.isPresent() && parentFromRepository.get().getType() == SystemItemType.FILE) {
                    log.error("SystemItemImport with ParentId: {} | have a file parent",
                            systemItemImport.getParentId());
                    throw new ValidationException("SystemItemImport with ParentId: {} | The parent of a FILE or FOLDER can only be a FOLDER");
                }

            }
        }
    }

    /**
     * Проверка на: поле url при импорте папки всегда должно быть равно null
     */
    private void checkUrlOfFolder(SystemItemImportRequest request) throws ValidationException {
        for (SystemItemImport systemItemImport : request.getItems()) {
            if (systemItemImport.getType() == SystemItemType.FOLDER && systemItemImport.getUrl() != null) {
                log.error("SystemItemImport with id: {} | url for FOLDER is NULL", systemItemImport.getId());
                throw new ValidationException("SystemItemImport with id: {} | url for FOLDER should be NULL");
            }
        }
    }

    /**
     * Проверка на: размер поля url при импорте файла всегда должен быть меньше либо равным 255
     */
    private void checkUrlSize(SystemItemImportRequest request) throws ValidationException {
        for (SystemItemImport systemItemImport : request.getItems()) {
            if (systemItemImport.getType() == SystemItemType.FILE &&
                    systemItemImport.getUrl() != null && systemItemImport.getUrl().length() > 255) {
                log.error("SystemItemImport with id: {} | url length dont less or equal than 255", systemItemImport.getId());
                throw new ValidationException("SystemItemImport with id: {} | url length should be less or equal than 255");
            }
        }
    }

    /**
     * Проверка на: поле size при импорте папки всегда должно быть равно null
     */
    private void checkFolderSize(SystemItemImportRequest request) throws ValidationException {
        for (SystemItemImport systemItemImport : request.getItems()) {
            if (systemItemImport.getType() == SystemItemType.FOLDER &&
                    systemItemImport.getSize() != null) {
                log.error("SystemItemImport with id: {} | size for FOLDER is NULL", systemItemImport.getId());
                throw new ValidationException("SystemItemImport with id: {} | size for FOLDER should be NULL");
            }
        }
    }

    /**
     * Проверка на: поле size для файлов всегда должно быть больше 0
     */
    private void checkFileSize(SystemItemImportRequest request) throws ValidationException {
        for (SystemItemImport systemItemImport : request.getItems()) {
            if (systemItemImport.getType() == SystemItemType.FILE &&
                    systemItemImport.getSize() <= 0) {
                log.error("SystemItemImport with id: {} | Folder size less than 0", systemItemImport.getId());
                throw new ValidationException("SystemItemImport with id: {} | size for Folder should be more than 0");
            }
        }
    }

}
