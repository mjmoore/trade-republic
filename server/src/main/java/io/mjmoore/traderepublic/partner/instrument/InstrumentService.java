package io.mjmoore.traderepublic.partner.instrument;

import io.mjmoore.traderepublic.instrument.Instrument;
import io.mjmoore.traderepublic.instrument.InstrumentRepository;
import io.mjmoore.traderepublic.mappers.InstrumentMapper;
import io.mjmoore.traderepublic.partner.DataService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.Instant;

@Slf4j
@Service
public class InstrumentService implements DataService<Instrument> {

    private final InstrumentRepository repository;
    private final InstrumentMapper mapper;

    public InstrumentService(final InstrumentRepository repository, final InstrumentMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    @Transactional
    public void accept(final Instrument instrument) {
        switch (instrument.type()) {
            case Add -> add(instrument);
            case Delete -> remove(instrument);
            default -> log.warn("Received an unknown instrument type {}", instrument.type().getType());
        }
    }

    private void remove(final Instrument instrument) {
        repository.findByIsin(instrument.isin())
                .map(entity -> entity.toBuilder()
                        .deleted(Instant.now())
                        .build())
                .ifPresent(entity -> {
                    repository.save(entity);
                    log.info("Deleted ISIN: {}", entity.getIsin());
                });
    }

    private void add(final Instrument instrument) {
        repository.findByIsin(instrument.isin())
                .ifPresentOrElse(
                        (entity) -> log.warn("Attempted to save duplicate ISIN: {}. Ignoring.", entity.getIsin()),
                        () -> repository.save(mapper.toEntity(instrument))
                );
    }
}
