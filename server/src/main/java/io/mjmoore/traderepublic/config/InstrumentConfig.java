package io.mjmoore.traderepublic.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.mjmoore.traderepublic.instrument.Instrument;
import io.mjmoore.traderepublic.mappers.InstrumentMapper;
import io.mjmoore.traderepublic.partner.DataHandler;
import io.mjmoore.traderepublic.partner.instrument.InstrumentDto;
import io.mjmoore.traderepublic.partner.instrument.InstrumentService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.client.WebSocketClient;
import reactor.core.publisher.Mono;

import java.net.URI;

@Configuration
public class InstrumentConfig {

    @Bean
    public Mono<Void> instrumentStream(final WebSocketClient client,
                                       final URI instrumentsUri,
                                       final WebSocketHandler instrumentsHandler) {

        return client.execute(instrumentsUri, instrumentsHandler);
    }

    @Bean
    public URI instrumentsUri(final PartnerConfig partnerConfig) {
        return URI.create(partnerConfig.getInstrumentsUrl());
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
