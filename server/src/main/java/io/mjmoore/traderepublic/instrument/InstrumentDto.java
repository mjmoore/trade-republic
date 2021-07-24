package io.mjmoore.traderepublic.instrument;

import io.mjmoore.traderepublic.instrument.InstrumentData;
import io.mjmoore.traderepublic.instrument.InstrumentType;

public record InstrumentDto(InstrumentData data, InstrumentType type) {
}
