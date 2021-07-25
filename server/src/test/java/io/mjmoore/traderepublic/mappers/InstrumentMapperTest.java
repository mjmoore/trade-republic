package io.mjmoore.traderepublic.mappers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.mjmoore.traderepublic.instrument.Instrument;
import io.mjmoore.traderepublic.partner.instrument.InstrumentData;
import io.mjmoore.traderepublic.partner.instrument.InstrumentDto;
import io.mjmoore.traderepublic.partner.instrument.InstrumentType;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

public class InstrumentMapperTest {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final InstrumentMapper mapper = new InstrumentMapper(objectMapper);

    private final InstrumentDto instrumentDto
            = new InstrumentDto(new InstrumentData("description", "isin"), InstrumentType.Add);

    @Test
    public void verifyMapping() {
        final Instrument instrument = mapper.toModel(instrumentDto);

        assertThat(instrument.date()).isNotNull();
        assertThat(instrument.isin()).isEqualTo(instrumentDto.data().isin());
        assertThat(instrument.description()).isEqualTo(instrumentDto.data().description());
    }

    @Test
    public void verifyDeserialization() throws JsonProcessingException {
        final String payload = objectMapper.writeValueAsString(instrumentDto);

        final Optional<InstrumentDto> instrument = mapper.toDto(payload);
        assertThat(instrument.isPresent()).isTrue();
    }

    @Test
    public void malformedPayload() {
        final Optional<InstrumentDto> instrument = mapper.toDto("malformed");
        assertThat(instrument.isEmpty()).isTrue();
    }
}
