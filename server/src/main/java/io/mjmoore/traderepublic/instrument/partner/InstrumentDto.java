package io.mjmoore.traderepublic.instrument.partner;

import io.mjmoore.traderepublic.instrument.partner.InstrumentData;
import io.mjmoore.traderepublic.instrument.partner.InstrumentType;

public record InstrumentDto(InstrumentData data, InstrumentType type) {
}
