package io.mjmoore.traderepublic.instrument;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

@Repository
public interface InstrumentRepository extends CrudRepository<InstrumentEntity, Integer> {

    default Optional<InstrumentEntity> findByIsin(final String isin) {
        return findByIsinAndDeletedIsNull(isin);
    }

    Set<InstrumentEntity> findByDeletedIsNull();

    Optional<InstrumentEntity> findByIsinAndDeletedIsNull(final String isin);
}
