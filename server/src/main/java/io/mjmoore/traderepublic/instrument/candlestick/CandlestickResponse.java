package io.mjmoore.traderepublic.instrument.candlestick;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class CandlestickResponse {

    @JsonIgnore
    @Builder.Default
    private Instant openPriceTime = Instant.MAX;

    @JsonIgnore
    @Builder.Default
    private Instant closePriceTime = Instant.MIN;

    private Instant openTime;
    private Instant closeTime;

    @Builder.Default
    private Double high = Double.MIN_VALUE;
    @Builder.Default
    private Double low = Double.MAX_VALUE;

    private Double open;
    private Double close;

}
