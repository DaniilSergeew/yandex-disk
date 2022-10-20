package com.example.yandexdisk.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class SystemItem {
    private String id;

    private String url;

    private String parentId;

    private LocalDateTime date;

    private SystemItemType systemItemType;

    private Integer size;
}
