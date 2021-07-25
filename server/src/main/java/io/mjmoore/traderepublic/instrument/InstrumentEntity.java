package io.mjmoore.traderepublic.instrument;

import io.mjmoore.traderepublic.quote.QuoteEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.Instant;
import java.util.Set;

@Data()
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)

@Entity
@Table(name = "Instrument")
public class InstrumentEntity {

    @Id
    @GeneratedValue
    private Integer instrumentId;

    @Column
    private Instant created;

    @Column
    private Instant deleted;

    @Column
    private String isin;

    @Column
    private String description;

    @OneToMany(mappedBy = "instrumentId", fetch = FetchType.EAGER)
    private Set<QuoteEntity> quotes;
}
