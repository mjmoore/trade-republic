package io.mjmoore.traderepublic.instrument;

import io.mjmoore.traderepublic.partner.instrument.InstrumentType;

import java.time.Instant;

public record Instrument(Instant date, String isin, String description, InstrumentType type) {
}
