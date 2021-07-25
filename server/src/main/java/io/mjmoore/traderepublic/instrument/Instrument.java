package io.mjmoore.traderepublic.instrument;

import io.mjmoore.traderepublic.instrument.partner.InstrumentType;

import java.time.Instant;

public record Instrument(Instant date, String isin, String description, InstrumentType type) {
}
