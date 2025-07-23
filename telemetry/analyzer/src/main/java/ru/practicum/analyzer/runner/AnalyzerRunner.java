package ru.practicum.analyzer.runner;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import ru.practicum.analyzer.processors.HubEventProcessor;
import ru.practicum.analyzer.processors.SnapshotProcessor;

@Component
@RequiredArgsConstructor
public class AnalyzerRunner implements CommandLineRunner {
    private final HubEventProcessor hubEventProcessor;
    private final SnapshotProcessor snapshotProcessor;

    @Override
    public void run(String... args) {
        Thread hubEventsThread = new Thread(hubEventProcessor);
        hubEventsThread.setName("HubEventHandlerThread");
        hubEventsThread.start();

        snapshotProcessor.start();
    }
}
