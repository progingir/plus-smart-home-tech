package ru.yandex.practicum.kafka.telemetry;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Collector {
    public static void main(String[] args) {
        SpringApplication.run(Collector.class, args);
    }
}

