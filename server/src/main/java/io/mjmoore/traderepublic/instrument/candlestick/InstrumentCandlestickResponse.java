package io.mjmoore.traderepublic.instrument.candlestick;

import io.mjmoore.traderepublic.instrument.InstrumentResponse;

import java.util.List;

public record InstrumentCandlestickResponse(InstrumentResponse instrument, List<CandlestickResponse> candlesticks) {
}
