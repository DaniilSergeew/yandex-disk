package com.example.yandexdisk;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class YandexDiskApplication {

    public static void main(String[] args) {
        SpringApplication.run(YandexDiskApplication.class, args);
    }
// CREATE CONSTRAINT ID_UNIQUE FOR (s:SystemItem) REQUIRE s.id IS UNIQUE
}
