package io.mjmoore.traderepublic.partner.quote;

import io.mjmoore.traderepublic.instrument.InstrumentEntity;
import io.mjmoore.traderepublic.instrument.InstrumentRepository;
import io.mjmoore.traderepublic.quote.Quote;
import io.mjmoore.traderepublic.quote.QuoteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class QuoteServiceTest {

    @Mock private InstrumentRepository instrumentRepository;
    @Mock private QuoteRepository quoteRepository;

    private QuoteService service;

    private final InstrumentEntity instrument = InstrumentEntity.builder()
            .isin("isin")
            .instrumentId(1)
            .build();

    private final Quote quote = new Quote(Instant.now(), "isin", 1.0, QuoteType.Quote);

    @BeforeEach
    public void setup() {
        service = new QuoteService(instrumentRepository, quoteRepository);
    }

    @Test
    public void newQuote() {
        when(instrumentRepository.findByIsin(anyString())).thenReturn(Optional.of(instrument));

        service.accept(quote);

        verify(quoteRepository, times(1)).save(any());
    }

    @Test
    public void unknownQuote() {
        when(instrumentRepository.findByIsin(anyString())).thenReturn(Optional.empty());

        service.accept(quote);

        verifyNoInteractions(quoteRepository);
    }
}
