package com.example.yandexdisk.dto;

import com.example.yandexdisk.model.SystemItemType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class SystemItemExport {
    private String id;

    private String url;

    private String parentId;

    private LocalDateTime date;

    private SystemItemType type;

    private Integer size;

    List<SystemItemExport> children;
}
