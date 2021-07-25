package io.mjmoore.traderepublic.instrument.candlestick;

import io.mjmoore.traderepublic.instrument.InstrumentResponse;

import java.util.Set;

public record InstrumentCandlestickResponse(InstrumentResponse instrument, Set<CandlestickResponse> candlesticks) {
}
