package ru.yandex.practicum.telemetry.analyzer.processor;


import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.*;
import ru.yandex.practicum.telemetry.analyzer.configuration.KafkaAnalyzerConfig;
import ru.yandex.practicum.telemetry.analyzer.entity.Scenario;
import ru.yandex.practicum.telemetry.analyzer.entity.Sensor;
import ru.yandex.practicum.telemetry.analyzer.mapper.ScenarioMapper;
import ru.yandex.practicum.telemetry.analyzer.mapper.SensorMapper;
import ru.yandex.practicum.telemetry.analyzer.service.ScenarioService;
import ru.yandex.practicum.telemetry.analyzer.service.SensorService;

import java.time.Duration;
import java.util.List;

@Slf4j
@Component
public class HubEventProcessor implements Runnable {

    private final KafkaConsumer<String, HubEventAvro> hubConsumer;
    private final KafkaAnalyzerConfig kafkaConfig;

    private final SensorService sensorService;
    private final ScenarioService scenarioService;

    public HubEventProcessor(KafkaAnalyzerConfig kafkaConfig,
                             SensorService sensorService,
                             ScenarioService scenarioService) {
        this.kafkaConfig = kafkaConfig;
        hubConsumer = new KafkaConsumer<>(kafkaConfig.getHubConsumerProperties());
        this.sensorService = sensorService;
        this.scenarioService = scenarioService;
    }


    @Override
    public void run() {
        log.info("HubEventProcessor started");

        try(hubConsumer) {
            Runtime.getRuntime().addShutdownHook(new Thread(hubConsumer::wakeup));
            hubConsumer.subscribe(List.of(kafkaConfig.getTopics().get("hubs-events")));

            while (true) {
                ConsumerRecords<String, HubEventAvro> records = hubConsumer.poll(Duration.ofSeconds(5));
                for (ConsumerRecord<String, HubEventAvro> record : records) {
                    HubEventAvro hubEventAvro = record.value();
                    switch (hubEventAvro.getPayload()) {

                        case DeviceAddedEventAvro deviceAddedEventAvro -> {
                            Sensor addedSensor = SensorMapper.avroToSensor(hubEventAvro.getHubId(), deviceAddedEventAvro);
                            sensorService.add(addedSensor);
                            break;
                        }

                        case DeviceRemovedEventAvro deviceRemovedEventAvro -> {
                            sensorService.delete(hubEventAvro.getHubId(), deviceRemovedEventAvro.getId());
                            break;
                        }


                        case ScenarioAddedEventAvro scenarioAddedEventAvro -> {
                            Scenario addedScenario = ScenarioMapper.avroToScenario(hubEventAvro.getHubId(), scenarioAddedEventAvro);
                            scenarioService.add(addedScenario);
                            break;
                        }

                        case ScenarioRemovedEventAvro scenarioRemovedEventAvro -> {
                            scenarioService.delete(scenarioRemovedEventAvro.getName());
                            break;
                        }

                        default -> throw new IllegalStateException("Unexpected value: " + hubEventAvro.getPayload());
                    }
                }

                hubConsumer.commitAsync((offsets, exception) -> {
                    if (exception != null) {
                        log.warn("Commit hubEvent processing error. Offsets: {}", offsets, exception);
                    }
                });
            }
        } catch (WakeupException ignored) {

        } catch (Exception e) {
                log.error("Analyzer. Error by handling HubEvents from kafka", e);
        } finally {
                log.info("Analyzer. Closing consumer.");
                hubConsumer.close();

        }
    }

}
