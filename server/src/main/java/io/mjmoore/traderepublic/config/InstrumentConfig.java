package io.mjmoore.traderepublic.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.mjmoore.traderepublic.instrument.Instrument;
import io.mjmoore.traderepublic.instrument.partner.InstrumentDto;
import io.mjmoore.traderepublic.instrument.partner.InstrumentService;
import io.mjmoore.traderepublic.mappers.InstrumentMapper;
import io.mjmoore.traderepublic.websocket.DataHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.client.WebSocketClient;
import reactor.core.publisher.Mono;

import java.net.URI;

@Slf4j
@Configuration
public class InstrumentConfig {

    @Bean
    public Mono<Void> instrumentStream(final WebSocketClient client,
                                       final URI instrumentsUri,
                                       final WebSocketHandler instrumentsHandler) {

        return client.execute(instrumentsUri, instrumentsHandler);
    }

    @Bean
    public URI instrumentsUri(@Value("${partner.instruments}") final String url) {
        return URI.create(url);
    }

    @Bean
    public DataHandler<InstrumentDto, Instrument> instrumentsHandler(final InstrumentMapper mapper,
                                                                     final InstrumentService service) {
        return new DataHandler<>(mapper, service);
    }

    @Bean
    public InstrumentMapper instrumentMapper(final ObjectMapper mapper) {
        return new InstrumentMapper(mapper);
    }

}
