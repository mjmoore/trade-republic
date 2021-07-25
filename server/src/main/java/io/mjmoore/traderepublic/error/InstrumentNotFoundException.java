package io.mjmoore.traderepublic.error;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.text.MessageFormat;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public final class InstrumentNotFoundException extends RuntimeException {

    private final static String message = "ISIN {0} not found.";

    public InstrumentNotFoundException(final String isin) {
        super(MessageFormat.format(message, isin));
    }

}
