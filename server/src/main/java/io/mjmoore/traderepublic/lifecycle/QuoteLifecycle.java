package io.mjmoore.traderepublic.lifecycle;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class QuoteLifecycle {

    private final Mono<Void> quoteStream;

    public QuoteLifecycle(final Mono<Void> quoteStream) {
        this.quoteStream = quoteStream;
    }

    @EventListener
    public void onStartUp(final ApplicationStartedEvent event) {
        this.quoteStream.subscribe();
        log.info("Quote collection started.");
    }
}
