package io.mjmoore.traderepublic.mappers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.mjmoore.traderepublic.instrument.Instrument;
import io.mjmoore.traderepublic.instrument.partner.InstrumentData;
import io.mjmoore.traderepublic.instrument.partner.InstrumentDto;
import io.mjmoore.traderepublic.instrument.InstrumentEntity;
import lombok.extern.slf4j.Slf4j;

import java.time.Instant;
import java.util.Optional;

@Slf4j
public class InstrumentMapper implements Mapper<InstrumentDto, Instrument> {

    private final ObjectMapper mapper;

    public InstrumentMapper(final ObjectMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public Optional<InstrumentDto> toDto(final String payload) {
        try {
            return Optional.of(mapper.readValue(payload, InstrumentDto.class));
        } catch (JsonProcessingException e) {
            log.warn("Malformed instrument payload: {}", payload);
        }
        return Optional.empty();
    }

    @Override
    public Instrument toModel(final InstrumentDto instrument) {
        final InstrumentData data = instrument.data();
        return new Instrument(Instant.now(), data.isin(), data.description(), instrument.type());
    }

    public InstrumentEntity toEntity(final Instrument instrument) {
        return InstrumentEntity.builder()
                .created(instrument.date())
                .isin(instrument.isin())
                .description(instrument.description())
                .build();
    }
}
