package com.example.yandexdisk.service.api;

import com.example.yandexdisk.dto.request.SystemItemImportRequest;
import com.example.yandexdisk.exception.ValidationException;
import com.example.yandexdisk.service.YandexDiskService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class YandexDiskApiService {
    private final YandexDiskService service;

    public void saveSystemItems(SystemItemImportRequest request) throws ValidationException {
        log.info("Trying to save SystemItems...");
        service.handleSystemItemImportRequest(request);
        log.info("Save was successful");
    }

    public void deleteSystemItemsById(UUID id) {
        log.info("Trying to delete SystemItems by id: {}...", id);
        service.handleSystemItemDeleteRequest(id);
        log.info("Deleting SystemItems by id: {} was successful", id);
    }

    public void getSystemItemsById(UUID id) {
        log.info("Trying to get Sales List by id: {}...", id);
        service.handleSystemItemGetRequest(id);
        log.info("Getting SystemItems by id: {} was successful", id);
    }

}
