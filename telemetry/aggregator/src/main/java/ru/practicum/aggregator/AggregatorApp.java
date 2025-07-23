package ru.practicum.aggregator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import ru.practicum.aggregator.starter.AggregatorStarter;

@SpringBootApplication
public class AggregatorApp {
    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(AggregatorApp.class, args);
        AggregatorStarter starter = context.getBean(AggregatorStarter.class);
        starter.start();
    }
}
