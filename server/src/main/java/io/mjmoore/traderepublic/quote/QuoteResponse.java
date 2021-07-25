package io.mjmoore.traderepublic.quote;

import java.time.Instant;

public record QuoteResponse(Instant time, Double price) {
}
