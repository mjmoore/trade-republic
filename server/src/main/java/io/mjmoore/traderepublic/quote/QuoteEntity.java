package io.mjmoore.traderepublic.quote;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)

@Entity
@Table(name = "Quote")
public class QuoteEntity {

    @Id
    @GeneratedValue
    private Integer quoteId;

    @Column
    private Instant time;

    @Column
    private Double price;

    @Column
    private Integer instrumentId;
}
