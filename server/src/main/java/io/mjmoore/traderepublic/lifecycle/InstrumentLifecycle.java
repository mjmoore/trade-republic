package io.mjmoore.traderepublic.lifecycle;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class InstrumentLifecycle {

    private final Mono<Void> instrumentStream;

    public InstrumentLifecycle(final Mono<Void> instrumentStream) {
        this.instrumentStream = instrumentStream;
    }

    @EventListener
    public void onStartUp(final ApplicationStartedEvent event) {
        this.instrumentStream.subscribe();
        log.info("Instrument collection started.");
    }
}
