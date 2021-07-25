package io.mjmoore.traderepublic.mappers;

import java.util.Optional;

public interface Mapper<Dto, Model> {
    Optional<Dto> toDto(final String payload);

    Model toModel(final Dto dto);
}
