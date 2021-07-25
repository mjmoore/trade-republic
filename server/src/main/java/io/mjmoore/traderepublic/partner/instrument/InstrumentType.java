package io.mjmoore.traderepublic.partner.instrument;

import com.fasterxml.jackson.annotation.JsonValue;

public enum InstrumentType {
    Add("ADD"),
    Delete("DELETE");

    private final String type;

    InstrumentType(final String type) {
        this.type = type;
    }

    @JsonValue
    public String getType() {
        return this.type;
    }
}
