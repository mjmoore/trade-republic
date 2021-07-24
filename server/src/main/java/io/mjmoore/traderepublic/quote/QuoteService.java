package io.mjmoore.traderepublic.quote;

import io.mjmoore.traderepublic.websocket.DataCollector;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class QuoteService implements DataCollector<QuoteDto> {

    private final Map<String, List<Quote>> quotes = new HashMap<>();

    @Override
    public void accept(final QuoteDto quote) {
        if (quote.type() == QuoteType.Quote) {
            addQuote(quote);
            return;
        }

        log.warn("Received an unknown quote type {}", quote.type().getType());
    }

    private void addQuote(final QuoteDto quoteDto) {
        // TODO: needs to validate against existing ISINs
        final Quote quote = new Quote(Instant.now(), quoteDto.data());
        final String isin = quote.data().isin();

        if(!quotes.containsKey(isin)) {
            quotes.put(isin, new ArrayList<>());
            log.info("Created new quote index: {}", isin);
        }

        quotes.get(isin).add(quote);
        log.info("Added new quote for {}: {}", isin, quote.data().price());
    }
}
