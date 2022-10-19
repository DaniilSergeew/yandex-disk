package com.example.yandexdisk.controller;

import com.example.yandexdisk.dto.request.SystemItemImportRequest;
import com.example.yandexdisk.exception.ValidationException;
import com.example.yandexdisk.service.api.YandexDiskApiService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Tag(name = "Контроллер для работы с диском")
public class YandexDiskController {
    private final YandexDiskApiService service;

    @Operation(
            summary = "Импортирует элементы файловой системы."
    )
    @PostMapping("/imports")
    public void importSystemItems(@Valid SystemItemImportRequest request) throws ValidationException {
        service.saveSystemItems(request);
    }

    @Operation(
            summary = "Удалить элемент по идентификатору. При удалении папки удаляются все дочерние элементы."
    )
    @DeleteMapping("delete/{id}")
    public void deleteSystemItems(@PathVariable UUID id) {
        service.deleteSystemItemsById(id);
    }

    @Operation(
            summary = "Получить информацию об элементе по идентификатору."
    )
    @GetMapping("nodes/{id}")
    public void getNodesById(@PathVariable UUID id) {
        service.getSystemItemsById(id);
    }

}
