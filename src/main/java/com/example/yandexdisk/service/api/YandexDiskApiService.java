package com.example.yandexdisk.service.api;

import com.example.yandexdisk.service.YandexDiskService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class YandexDiskApiService {
    private final YandexDiskService service;

}
