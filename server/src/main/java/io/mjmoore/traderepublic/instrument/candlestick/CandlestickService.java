package io.mjmoore.traderepublic.instrument.candlestick;

import io.mjmoore.traderepublic.instrument.InstrumentEntity;
import io.mjmoore.traderepublic.instrument.InstrumentRepository;
import io.mjmoore.traderepublic.quote.QuoteEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@Service
public class CandlestickService {

    private final InstrumentRepository instrumentRepository;

    public CandlestickService(final InstrumentRepository instrumentRepository) {
        this.instrumentRepository = instrumentRepository;
    }

    public List<CandlestickResponse> getCandlesticks(final String isin) {

        return instrumentRepository.findByIsin(isin)
                .map(InstrumentEntity::getQuotes)
                .stream()
                .flatMap(Collection::stream)
                .collect(new CandlestickCollector());
    }

    private static class CandlestickCollector
            implements Collector<QuoteEntity, Map<Instant, Set<QuoteEntity>>, List<CandlestickResponse>> {


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
        public Function<Map<Instant, Set<QuoteEntity>>, List<CandlestickResponse>> finisher() {
            return (data) -> {
                final List<CandlestickResponse> candlesticks = data.entrySet()
                        .stream()
                        .sorted(Map.Entry.comparingByKey())
                        .map(entry -> entry.getValue()
                                .stream()
                                .collect(new CandlestickMaker(entry.getKey())))
                        .collect(Collectors.toList());

                if(candlesticks.size() <= 1) {
                    return candlesticks;
                }

                final List<CandlestickResponse> gaps = IntStream.range(1, candlesticks.size())
                        .mapToObj(i -> Pair.of(candlesticks.get(i - 1), candlesticks.get(i)))
                        .map(pair -> {
                            final long minutesBetween = Duration
                                    .between(pair.getLeft().getOpenTime(), pair.getRight().getOpenTime())
                                    .toMinutes() - 1;

                            if (minutesBetween == 0) {
                                return Collections.<CandlestickResponse>emptyList();
                            }

                            return Stream.generate(new DuplicateCandlestickGenerator(pair.getLeft()))
                                    .limit(minutesBetween)
                                    .collect(Collectors.toList());
                        })
                        .flatMap(Collection::stream)
                        .collect(Collectors.toList());

                return Stream.of(candlesticks, gaps)
                        .flatMap(Collection::stream)
                        .sorted(Comparator.comparing(CandlestickResponse::getOpenTime))
                        .collect(Collectors.toList());

            };
        }

        private static class DuplicateCandlestickGenerator implements Supplier<CandlestickResponse> {

            private final CandlestickResponse candlestick;
            private int counter = 1;

            private DuplicateCandlestickGenerator(final CandlestickResponse candlestick) {
                this.candlestick = candlestick;
            }

            @Override
            public CandlestickResponse get() {
                return candlestick.toBuilder()
                        .openTime(candlestick.getOpenTime().plus(counter++, ChronoUnit.MINUTES))
                        .closeTime(candlestick.getOpenTime().plus(counter, ChronoUnit.MINUTES))
                        .build();
            }
        }
        @Data
        @AllArgsConstructor(staticName = "of")
        private static class Pair {
            private CandlestickResponse left;
            private CandlestickResponse right;
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
