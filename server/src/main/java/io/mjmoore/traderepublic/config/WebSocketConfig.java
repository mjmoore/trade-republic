package io.mjmoore.traderepublic.config;

import com.fasterxml.jackson.core.type.TypeReference;
import io.mjmoore.traderepublic.instrument.Instrument;
import io.mjmoore.traderepublic.instrument.InstrumentDto;
import io.mjmoore.traderepublic.quote.Quote;
import io.mjmoore.traderepublic.quote.QuoteDto;
import io.mjmoore.traderepublic.websocket.DataCollector;
import io.mjmoore.traderepublic.websocket.WebSocketHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;

import java.net.URI;
import java.util.concurrent.ExecutionException;

@Configuration
public class WebSocketConfig {

    @Bean
    public WebSocketClient client() {
        return new StandardWebSocketClient();
    }

    @Bean
    public WebSocketSession instruments(final WebSocketClient client,
                                        final WebSocketHandler<InstrumentDto> handler,
                                        final WebSocketHttpHeaders httpHeaders,
                                        @Value("${partner.instruments}") final String url)
            throws ExecutionException, InterruptedException {

        return client.doHandshake(handler, httpHeaders, URI.create(url)).get();
    }

    @Bean
    public WebSocketSession quotes(final WebSocketClient client,
                                   final WebSocketHandler<QuoteDto> handler,
                                   final WebSocketHttpHeaders httpHeaders,
                                   @Value("${partner.quotes}") final String url)
            throws ExecutionException, InterruptedException {

        return client.doHandshake(handler, httpHeaders, URI.create(url)).get();
    }

    @Bean
    public WebSocketHttpHeaders webSocketHttpHeaders() {
        return new WebSocketHttpHeaders();
    }

    @Bean
    public TypeReference<QuoteDto> quoteType() {
        return new TypeReference<>() {
        };
    }

    @Bean
    public TypeReference<InstrumentDto> instrumentType() {
        return new TypeReference<>() {
        };
    }

    @Bean
    public WebSocketHandler<QuoteDto> quoteHandler(final TypeReference<QuoteDto> type,
                                                   final DataCollector<QuoteDto> quoteCollector) {
        return new WebSocketHandler<>(quoteCollector, type);
    }

    @Bean
    public WebSocketHandler<InstrumentDto> instrumentHandler(final TypeReference<InstrumentDto> type,
                                                             final DataCollector<InstrumentDto> instrumentCollector) {
        return new WebSocketHandler<>(instrumentCollector, type);
    }
}
