package io.mjmoore.traderepublic.mappers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.mjmoore.traderepublic.quote.Quote;
import io.mjmoore.traderepublic.quote.QuoteEntity;
import io.mjmoore.traderepublic.quote.partner.QuoteData;
import io.mjmoore.traderepublic.quote.partner.QuoteDto;
import lombok.extern.slf4j.Slf4j;

import java.time.Instant;
import java.util.Optional;

@Slf4j
public class QuoteMapper implements Mapper<QuoteDto, Quote> {

    private final ObjectMapper mapper;

    public QuoteMapper(final ObjectMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public Optional<QuoteDto> toDto(final String payload) {
        try {
            return Optional.of(mapper.readValue(payload, QuoteDto.class));
        } catch (JsonProcessingException e) {
            log.warn("Malformed quote payload: {}", payload);
        }
        return Optional.empty();
    }

    @Override
    public Quote toModel(final QuoteDto quote) {
        final QuoteData data = quote.data();
        return new Quote(Instant.now(), data.isin(), data.price(), quote.type());
    }
}
