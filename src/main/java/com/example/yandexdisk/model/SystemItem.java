package com.example.yandexdisk.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class SystemItem {
    private UUID id;

    private String url;

    private UUID parentId;

    private LocalDateTime date;

    private SystemItemType systemItemType;

    private Integer size;
}
