package com.example.yandexdisk.controller;

import com.example.yandexdisk.dto.request.SystemItemImportRequest;
import com.example.yandexdisk.exception.ValidationException;
import com.example.yandexdisk.service.api.YandexDiskApiService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class YandexDiskController {
    private final YandexDiskApiService service;

    @PostMapping("/imports")
    public void importSystemItems(@Valid SystemItemImportRequest request) throws ValidationException {
        service.saveSystemItems(request);
    }

    @DeleteMapping("delete/{id}")
    public void deleteSystemItems(@PathVariable UUID id) {
        service.deleteSystemItemsById(id);
    }

    @GetMapping("nodes/{id}")
    public void getNodesById(@PathVariable UUID id) {
        service.deleteSystemItemsById(id);
    }

}
