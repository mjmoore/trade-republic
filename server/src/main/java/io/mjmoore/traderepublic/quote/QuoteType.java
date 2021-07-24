package io.mjmoore.traderepublic.quote;

import com.fasterxml.jackson.annotation.JsonValue;

public enum QuoteType {
    Quote("QUOTE");

    private final String type;

    QuoteType(final String type) {
        this.type = type;
    }

    @JsonValue
    public String getType() {
        return this.type;
    }
}
