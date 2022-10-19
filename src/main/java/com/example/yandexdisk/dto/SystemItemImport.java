package com.example.yandexdisk.dto;

import com.example.yandexdisk.model.SystemItemType;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.UUID;

@Data
public class SystemItemImport {
    @NotNull
    private UUID id;

    private String url;

    private UUID parentId;

    private SystemItemType systemItemType;

    private Integer size;
}
