package io.mjmoore.traderepublic.instrument.candlestick;

import io.mjmoore.traderepublic.instrument.InstrumentEntity;
import io.mjmoore.traderepublic.instrument.InstrumentRepository;
import io.mjmoore.traderepublic.quote.QuoteEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CandlestickServiceTest {

    private final static Instant now = Instant.now()
            .atZone(ZoneOffset.UTC)
            .withYear(2021)
            .withMonth(7)
            .withDayOfMonth(25)
            .withHour(15)
            .withMinute(25)
            .withSecond(0)
            .withNano(0)
            .toInstant();

    @Mock private InstrumentRepository repository;

    private CandlestickService service;

    @BeforeEach
    public void setup() {
        service = new CandlestickService(repository);
    }

    @Test
    public void noQuotes() {
        when(repository.findByIsin(any())).thenReturn(Optional.of(InstrumentEntity.builder().build()));

        assertThat(service.getCandlesticks("isin")).isEmpty();
    }

    @Test
    public void singleQuote() {

        final Set<QuoteDef> quotes = Set.of(new QuoteDef(now, 1.0));

        when(repository.findByIsin(any()))
                .thenReturn(Optional.of(generateInstrument(quotes)));

        final List<CandlestickResponse> candlesticks = service.getCandlesticks("isin");
        assertThat(candlesticks.size()).isEqualTo(1);

    }

    @Test
    public void openAndCloseTimesAreCorrectlySet() {
        final Set<QuoteDef> quotes = Set.of(new QuoteDef(now, 1.0));

        when(repository.findByIsin(any()))
                .thenReturn(Optional.of(generateInstrument(quotes)));

        final CandlestickResponse candlestick = service.getCandlesticks("isin").iterator().next();
        assertThat(candlestick.getOpenTime()).isEqualTo(now);
        assertThat(candlestick.getCloseTime()).isAfter(now);
    }

    @Test
    public void candlestickData() {

        final QuoteDef open = new QuoteDef(now, 5.0);
        final QuoteDef close = new QuoteDef(now.plusSeconds(59), 10.0);

        final QuoteDef high = new QuoteDef(now.plusSeconds(5), 100.0);
        final QuoteDef low = new QuoteDef(now.plusSeconds(5), 1.0);

        final Set<QuoteDef> quotes = Set.of(open, close, high, low);


        when(repository.findByIsin(any()))
                .thenReturn(Optional.of(generateInstrument(quotes)));

        final CandlestickResponse candlestick = service.getCandlesticks("isin").iterator().next();

        assertThat(candlestick.getOpen()).isEqualTo(open.getPrice());
        assertThat(candlestick.getClose()).isEqualTo(close.getPrice());

        assertThat(candlestick.getLow()).isEqualTo(low.getPrice());
        assertThat(candlestick.getHigh()).isEqualTo(high.getPrice());

    }

    @Test
    public void manyQuotesInOneMinute() {

        final Set<QuoteDef> quotes = Set.of(
                new QuoteDef(now.plusSeconds(5), 1.0),
                new QuoteDef(now.plusSeconds(10), 1.0),
                new QuoteDef(now.plusSeconds(15), 1.0),
                new QuoteDef(now.plusSeconds(20), 1.0),
                new QuoteDef(now.plusSeconds(25), 1.0)
        );

        when(repository.findByIsin(any()))
                .thenReturn(Optional.of(generateInstrument(quotes)));

        assertThat(service.getCandlesticks("isin").size()).isEqualTo(1);
    }

    @Test
    public void quotesSplitIntoGroups() {

        final Set<QuoteDef> quotes = Set.of(
                new QuoteDef(now.plusSeconds(5), 1.0),
                new QuoteDef(now.plusSeconds(10), 1.0),
                new QuoteDef(now.plusSeconds(65), 1.0),
                new QuoteDef(now.plusSeconds(70), 1.0),
                new QuoteDef(now.plusSeconds(120), 1.0)
        );

        when(repository.findByIsin(any()))
                .thenReturn(Optional.of(generateInstrument(quotes)));

        assertThat(service.getCandlesticks("isin").size()).isEqualTo(3);
    }

    @Test
    public void quotesAtSameTimeOnDifferentDays() {

        // Two explicit quotes and 1439 gaps (1440 minutes in a day, minus one for tomorrows quote)
        final QuoteDef today = new QuoteDef(now, 1.0);
        final QuoteDef tomorrow = new QuoteDef(now.plus(1, ChronoUnit.DAYS), 1.0);

        final Set<QuoteDef> quotes = Set.of(today, tomorrow);

        when(repository.findByIsin(any()))
                .thenReturn(Optional.of(generateInstrument(quotes)));

        final long gaps = Duration.between(today.getTime(), tomorrow.getTime()).toMinutes() - 1;
        assertThat(service.getCandlesticks("isin").size()).isEqualTo(2 + gaps);
    }

    @Test
    public void missingQuotesCopyPreviousCandlestick() {

        // Three explicit quotes, 1 gap -> 4 candle sticks
        final Set<QuoteDef> quotes = Set.of(
                new QuoteDef(now, 1.0),
                new QuoteDef(now.plusSeconds(120), 1.0),
                new QuoteDef(now.plusSeconds(180), 1.0)
        );

        when(repository.findByIsin(any()))
                .thenReturn(Optional.of(generateInstrument(quotes)));

        assertThat(service.getCandlesticks("isin").size()).isEqualTo(4);
    }

    private InstrumentEntity generateInstrument(final Set<QuoteDef> quoteDefs) {
        return InstrumentEntity.builder()
                .isin("isin")
                .quotes(quoteDefs.stream()
                        .map(quoteDef -> QuoteEntity.builder()
                            .price(quoteDef.getPrice())
                            .time(quoteDef.getTime())
                            .build())
                        .collect(Collectors.toSet()))
                .build();
    }

    @Data
    @AllArgsConstructor
    private static class QuoteDef {
        private Instant time;
        private Double price;
    }
}
