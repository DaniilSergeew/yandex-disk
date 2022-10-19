package com.example.yandexdisk.dto;

import com.example.yandexdisk.model.SystemItemType;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.UUID;

@Data
public class SystemItemImport {
    @NotNull
    private String id;

    private String url;

    private String parentId;

    private SystemItemType systemItemType;

    private Integer size;
}
