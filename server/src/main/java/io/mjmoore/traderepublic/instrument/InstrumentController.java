package io.mjmoore.traderepublic.instrument;

import io.mjmoore.traderepublic.error.InstrumentNotFoundException;
import io.mjmoore.traderepublic.instrument.candlestick.CandlestickResponse;
import io.mjmoore.traderepublic.instrument.candlestick.CandlestickService;
import io.mjmoore.traderepublic.instrument.candlestick.InstrumentCandlestickResponse;
import io.mjmoore.traderepublic.quote.QuoteResponse;
import io.mjmoore.traderepublic.quote.QuotesResponse;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/instruments")
public class InstrumentController {

    private final InstrumentRepository instrumentRepository;
    private final CandlestickService candlestickService;

    public InstrumentController(final InstrumentRepository instrumentRepository,
                                final CandlestickService candlestickService) {

        this.instrumentRepository = instrumentRepository;
        this.candlestickService = candlestickService;
    }

    @GetMapping
    @Operation(summary = "Get a list of active instruments.")
    public ResponseEntity<Set<InstrumentResponse>> getInstruments() {
        final Set<InstrumentResponse> instruments = instrumentRepository.findByDeletedIsNull()
                .stream()
                .map(this::mapInstrument)
                .collect(Collectors.toSet());

        return ResponseEntity.ok(instruments);
    }

    @GetMapping("/{isin}")
    @Operation(summary = "Get a summary of a known instrument.")
    public ResponseEntity<InstrumentResponse> getInstrument(@PathVariable final String isin) {

        return ResponseEntity.ok(findInstrument(isin));
    }

    @GetMapping("/{isin}/quotes")
    @Operation(summary = "Get a  list of raw quotes for a known instrument.")
    public ResponseEntity<QuotesResponse> getQuotes(@PathVariable final String isin) {
        final InstrumentEntity instrument = findInstrumentEntity(isin);

        final Set<QuoteResponse> quotes = instrument.getQuotes()
                .stream()
                .map(quote -> new QuoteResponse(quote.getTime(), quote.getPrice()))
                .collect(Collectors.toSet());

        return ResponseEntity.ok(new QuotesResponse(mapInstrument(instrument), quotes));
    }

    @GetMapping("/{isin}/candlesticks")
    @Operation(
            summary = "Get candlesticks for a known instrument.",
            description = """
                    Candlesticks are a temporal representation of price fluctuations for a given instrument.
                                       
                    Each candle stick represents a one minute time slice.
                    
                    A candlestick represents the open and close time, as well as high, low, open and close price positions.
                     """
    )
    public ResponseEntity<InstrumentCandlestickResponse> getCandlestick(@PathVariable final String isin) {

        final InstrumentResponse instrument = findInstrument(isin);

        final List<CandlestickResponse> candlesticks = this.candlestickService.getCandlesticks(isin);

        final InstrumentCandlestickResponse instrumentCandlestick =
                new InstrumentCandlestickResponse(instrument, candlesticks);

        return ResponseEntity.ok(instrumentCandlestick);
    }

    private InstrumentResponse findInstrument(final String isin) {
        final InstrumentEntity instrument= findInstrumentEntity(isin);
        return mapInstrument(instrument);
    }

    private InstrumentEntity findInstrumentEntity(final String isin) {
        return instrumentRepository.findByIsin(isin)
                .orElseThrow(() -> new InstrumentNotFoundException(isin));
    }

    private InstrumentResponse mapInstrument(final InstrumentEntity instrument) {
        return new InstrumentResponse(instrument.getIsin(), instrument.getDescription());
    }

}
