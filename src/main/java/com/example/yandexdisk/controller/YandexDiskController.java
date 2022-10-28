package com.example.yandexdisk.controller;

import com.example.yandexdisk.dto.SystemItemExport;
import com.example.yandexdisk.dto.request.SystemItemImportRequest;
import com.example.yandexdisk.exception.EntityNotFoundException;
import com.example.yandexdisk.exception.ValidationException;
import com.example.yandexdisk.service.api.YandexDiskApiService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@Tag(name = "Контроллер для работы с диском")
public class YandexDiskController {
    private final YandexDiskApiService service;

    @Operation(
            summary = "Импортирует элементы файловой системы.",
            description = "Элементы импортированные повторно обновляют текущие." +
                    "Изменение типа элемента с папки на файл и с файла на папку не допускается." +
                    "Порядок элементов в запросе является произвольным."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Import sucseful"),
            @ApiResponse(responseCode = "400", description = "RequestBody incorrect", content = @Content),
            @ApiResponse(responseCode = "500", description = "Unexpected error", content = @Content)
    })
    @PostMapping("/imports")
    public ResponseEntity<?> importSystemItems(@Valid @RequestBody SystemItemImportRequest request) throws ValidationException {
        service.saveSystemItems(request);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Operation(
            summary = "Удалить элемент по идентификатору. При удалении папки удаляются все дочерние элементы.",
            description = "При удалении папки удаляются все дочерние элементы." +
                    "Доступ к истории обновлений удаленного элемента невозможен."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Delete sucseful"),
            @ApiResponse(responseCode = "400", description = "Id incorrect", content = @Content),
            @ApiResponse(responseCode = "500", description = "Unexpected error", content = @Content)
    })
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteSystemItems(@PathVariable String id) {
        service.deleteSystemItemsById(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Operation(
            summary = "Получить информацию об элементе по идентификатору.",
            description = "При получении информации о папке также предоставляется информация о её дочерних элементах."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Getting sucseful"),
            @ApiResponse(responseCode = "400", description = "Id incorrect", content = @Content),
            @ApiResponse(responseCode = "500", description = "Unexpected error", content = @Content)
    })
    @GetMapping("/nodes/{id}")
    public ResponseEntity<SystemItemExport> getNodesById(@PathVariable String id) throws EntityNotFoundException {
        SystemItemExport response = service.getSystemItemsById(id);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}
