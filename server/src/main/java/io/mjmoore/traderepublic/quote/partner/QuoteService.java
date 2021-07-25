package io.mjmoore.traderepublic.quote.partner;

import io.mjmoore.traderepublic.instrument.InstrumentRepository;
import io.mjmoore.traderepublic.quote.Quote;
import io.mjmoore.traderepublic.quote.QuoteEntity;
import io.mjmoore.traderepublic.quote.QuoteRepository;
import io.mjmoore.traderepublic.websocket.DataService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Slf4j
@Service
public class QuoteService implements DataService<Quote> {

    private final InstrumentRepository instrumentRepository;
    private final QuoteRepository quoteRepository;

    public QuoteService(final InstrumentRepository instrumentRepository,
                        final QuoteRepository quoteRepository) {

        this.instrumentRepository = instrumentRepository;
        this.quoteRepository = quoteRepository;
    }

    @Override
    @Transactional
    public void accept(final Quote quote) {
        if (quote.type() == QuoteType.Quote) {
            addQuote(quote);
            return;
        }

        log.warn("Received an unknown quote type {}", quote.type().getType());
    }

    private void addQuote(final Quote quote) {

        instrumentRepository.findByIsin(quote.isin())
                .map(instrument -> QuoteEntity.builder()
                        .instrumentId(instrument.getInstrumentId())
                        .time(quote.time())
                        .price(quote.price())
                        .build())
                .map(quoteRepository::save)
                .ifPresentOrElse(
                        (entity) -> log.debug("New quote of {} for ISIN {}",
                                entity.getPrice(), quote.isin()),
                        () -> log.warn("Received quote for unknown or deleted ISIN: {}", quote.isin())
                );
    }
}
