package io.mjmoore.traderepublic.instrument.candlestick;

import io.mjmoore.traderepublic.instrument.InstrumentEntity;
import io.mjmoore.traderepublic.instrument.InstrumentRepository;
import io.mjmoore.traderepublic.quote.QuoteEntity;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collectors;

@Service
public class CandlestickService {

    private final InstrumentRepository instrumentRepository;

    public CandlestickService(final InstrumentRepository instrumentRepository) {
        this.instrumentRepository = instrumentRepository;
    }

    public Set<CandlestickResponse> getCandlesticks(final String isin) {

        return instrumentRepository.findByIsin(isin)
                .map(InstrumentEntity::getQuotes)
                .stream()
                .flatMap(Collection::stream)
                .collect(new CandlestickCollector());
    }

    private static class CandlestickCollector
            implements Collector<QuoteEntity, Map<Instant, Set<QuoteEntity>>, Set<CandlestickResponse>> {


        @Override
        public Supplier<Map<Instant, Set<QuoteEntity>>> supplier() {
            return HashMap::new;
        }

        @Override
        public BiConsumer<Map<Instant, Set<QuoteEntity>>, QuoteEntity> accumulator() {
            return (acc, quote) -> {
                final Instant key = quote.getTime().truncatedTo(ChronoUnit.MINUTES);
                acc.putIfAbsent(key, new HashSet<>());
                acc.get(key).add(quote);
            };
        }

        @Override
        public BinaryOperator<Map<Instant, Set<QuoteEntity>>> combiner() {
            // TODO
            return (left, right) -> left;
        }

        @Override
        public Function<Map<Instant, Set<QuoteEntity>>, Set<CandlestickResponse>> finisher() {
            return (data) -> data.entrySet()
                    .stream()
                    .map(entry -> entry.getValue()
                            .stream()
                            .collect(new CandlestickMaker(entry.getKey())))
                    .collect(Collectors.toSet());
        }

        @Override
        public Set<Characteristics> characteristics() {
            return Collections.emptySet();
        }
    }

    private record CandlestickMaker(Instant open)
            implements Collector<QuoteEntity, CandlestickResponse, CandlestickResponse> {

        @Override
        public Supplier<CandlestickResponse> supplier() {
            return () -> CandlestickResponse.builder()
                    .openTime(this.open)
                    .closeTime(this.open.plus(1, ChronoUnit.MINUTES))
                    .build();
        }

        @Override
        public BiConsumer<CandlestickResponse, QuoteEntity> accumulator() {
            return (candlestick, quote) -> {

                final Double price = quote.getPrice();

                if(candlestick.getHigh() < price) {
                    candlestick.setHigh(price);
                }

                if(candlestick.getLow() > price) {
                    candlestick.setLow(price);
                }

                final Instant time = quote.getTime();

                if(candlestick.getClosePriceTime().isBefore(time)) {
                    candlestick.setClosePriceTime(time);
                    candlestick.setClose(price);
                }

                if(candlestick.getOpenPriceTime().isAfter(time)) {
                    candlestick.setOpenPriceTime(time);
                    candlestick.setOpen(price);
                }
            };
        }

        @Override
        public BinaryOperator<CandlestickResponse> combiner() {
            // TODO
            return (left, right) -> left;
        }

        @Override
        public Function<CandlestickResponse, CandlestickResponse> finisher() {
            return Function.identity();
        }

        @Override
        public Set<Characteristics> characteristics() {
            return Collections.emptySet();
        }

    }
}
