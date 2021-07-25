package io.mjmoore.traderepublic.mappers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.mjmoore.traderepublic.partner.quote.QuoteData;
import io.mjmoore.traderepublic.partner.quote.QuoteDto;
import io.mjmoore.traderepublic.partner.quote.QuoteType;
import io.mjmoore.traderepublic.quote.Quote;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

public class QuoteMapperTest {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final QuoteMapper mapper = new QuoteMapper(objectMapper);

    private final QuoteDto quoteDto = new QuoteDto(new QuoteData(1.0, "isin"), QuoteType.Quote);

    @Test
    public void verifyMapping() {
        final Quote quote = mapper.toModel(quoteDto);

        assertThat(quote.time()).isNotNull();
        assertThat(quote.isin()).isEqualTo(quoteDto.data().isin());
        assertThat(quote.price()).isEqualTo(quoteDto.data().price());
        assertThat(quote.type()).isEqualTo(quoteDto.type());
    }

    @Test
    public void verifyDeserialization() throws JsonProcessingException {
        final String payload = objectMapper.writeValueAsString(quoteDto);

        final Optional<QuoteDto> quote = mapper.toDto(payload);
        assertThat(quote.isPresent()).isTrue();
    }

    @Test
    public void malformedPayload() {
        final Optional<QuoteDto> quote = mapper.toDto("malformed");
        assertThat(quote.isEmpty()).isTrue();
    }
}
