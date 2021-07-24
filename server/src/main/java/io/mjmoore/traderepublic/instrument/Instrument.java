package io.mjmoore.traderepublic.instrument;

import java.time.Instant;

public record Instrument(Instant time, InstrumentData data) {
}
