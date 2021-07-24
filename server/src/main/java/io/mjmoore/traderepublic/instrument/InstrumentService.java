package io.mjmoore.traderepublic.instrument;

import io.mjmoore.traderepublic.websocket.DataCollector;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
public class InstrumentService implements DataCollector<InstrumentDto> {

    private final Map<String, Instrument> instruments = new HashMap<>();

    @Override
    public void accept(final InstrumentDto instrument) {
        switch (instrument.type()) {
            case Add -> add(instrument);
            case Delete -> remove(instrument);
            default -> log.warn("Received an unknown instrument type {}", instrument.type().getType());
        }
    }

    private void remove(final InstrumentDto instrumentDto) {
        final Optional<Instrument> stored = Optional.ofNullable(instruments.remove(instrumentDto.data().isin()));

        stored.ifPresentOrElse(
                (instrument) -> log.info("ISIN {} removed.", instrument.data().isin()),
                () -> log.warn("Tried to remove ISIN {}, but wasn't found", instrumentDto.data().isin())
        );
    }

    private void add(final InstrumentDto instrument) {
        final String isin = instrument.data().isin();

        if(instruments.containsKey(isin)) {
            log.debug("Duplicate ISIN {}", isin);
            return;
        }
        instruments.put(isin, new Instrument(Instant.now(), instrument.data()));
        log.info("ISIN {} added", isin);
    }
}
