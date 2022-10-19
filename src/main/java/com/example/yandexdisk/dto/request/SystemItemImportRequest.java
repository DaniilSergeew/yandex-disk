package com.example.yandexdisk.dto.request;

import com.example.yandexdisk.dto.SystemItemImport;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class SystemItemImportRequest {
    @NotNull
    private List<SystemItemImport> items;

    @NotNull
    private LocalDateTime updateDate;
}
