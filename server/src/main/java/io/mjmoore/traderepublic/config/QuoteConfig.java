package io.mjmoore.traderepublic.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.mjmoore.traderepublic.mappers.QuoteMapper;
import io.mjmoore.traderepublic.quote.Quote;
import io.mjmoore.traderepublic.quote.partner.QuoteDto;
import io.mjmoore.traderepublic.quote.partner.QuoteService;
import io.mjmoore.traderepublic.websocket.DataHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.client.WebSocketClient;
import reactor.core.publisher.Mono;

import java.net.URI;

@Configuration
public class QuoteConfig {

    @Bean
    public Mono<Void> quoteStream(final WebSocketClient client,
                                  final URI quotesUri,
                                  final WebSocketHandler quoteHandler) {

        return client.execute(quotesUri, quoteHandler);
    }

    @Bean
    public DataHandler<QuoteDto, Quote> quoteHandler(final QuoteMapper mapper,
                                                     final QuoteService service) {

        return new DataHandler<>(mapper, service);
    }

    @Bean
    public URI quotesUri(@Value("${partner.quotes}") final String url) {
        return URI.create(url);
    }

    @Bean
    public QuoteMapper quoteMapper(final ObjectMapper mapper) {
        return new QuoteMapper(mapper);
    }
}
