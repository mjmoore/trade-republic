package io.mjmoore.traderepublic.quote;

import java.time.Instant;

public record Quote(Instant time, QuoteData data) {
}
