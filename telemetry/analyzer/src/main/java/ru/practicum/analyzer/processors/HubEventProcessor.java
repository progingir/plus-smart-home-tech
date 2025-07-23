package ru.practicum.analyzer.processors;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.practicum.analyzer.handlers.event.HubEventHandler;
import ru.practicum.analyzer.handlers.event.HubEventHandlers;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;

import java.time.Duration;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class HubEventProcessor implements Runnable {
    private final Consumer<String, HubEventAvro> consumer;
    private final HubEventHandlers handlers;
    @Value("${topic.hub-event-topic}")
    private String topic;

    @Override
    public void run() {

        try {
            consumer.subscribe(List.of(topic));
            Runtime.getRuntime().addShutdownHook(new Thread(consumer::wakeup));
            Map<String, HubEventHandler> handlerMap = handlers.getHandlers();

            while (true) {

                ConsumerRecords<String, HubEventAvro> records = consumer.poll(Duration.ofMillis(1000));

                for (ConsumerRecord<String, HubEventAvro> record : records) {
                    HubEventAvro event = record.value();
                    String payloadName = event.getPayload().getClass().getSimpleName();
                    log.info("Получили сообщение хаба типа: {}", payloadName);

                    if (handlerMap.containsKey(payloadName)) {
                        handlerMap.get(payloadName).handle(event);
                    } else {
                        throw new IllegalArgumentException("Не могу найти обработчик для события " + event);
                    }
                }

                consumer.commitSync();
            }
        } catch (WakeupException ignored) {
        } catch (Exception e) {
            log.error("Ошибка чтения данных из топика {}", topic);
        } finally {
            try {
                consumer.commitSync();
            } finally {
                consumer.close();
            }
        }
    }
}
