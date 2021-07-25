package io.mjmoore.traderepublic.quote;

import io.mjmoore.traderepublic.partner.quote.QuoteType;

import java.time.Instant;

public record Quote(Instant time, String isin, Double price, QuoteType type) {
}
