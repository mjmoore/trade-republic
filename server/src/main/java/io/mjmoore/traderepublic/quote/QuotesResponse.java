package io.mjmoore.traderepublic.quote;

import io.mjmoore.traderepublic.instrument.InstrumentResponse;

import java.util.Set;

public record QuotesResponse(InstrumentResponse instrument, Set<QuoteResponse> quotes){

}
