package io.mjmoore.traderepublic.partner;

import io.mjmoore.traderepublic.mappers.Mapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Mono;

import java.util.Optional;

@Slf4j
public class DataHandler<Dto, Model> implements WebSocketHandler {

    private final Mapper<Dto, Model> mapper;
    private final DataService<Model> dataService;

    public DataHandler(final Mapper<Dto, Model> mapper, final DataService<Model> dataService) {
        this.mapper = mapper;
        this.dataService = dataService;
    }

    @NonNull
    @Override
    public Mono<Void> handle(final WebSocketSession session) {

        return session.receive()
                .map(WebSocketMessage::getPayloadAsText)
                .map(mapper::toDto)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(mapper::toModel)
                .doOnNext(model -> log.debug("{}", model))
                .doOnNext(dataService)
                .then();
    }
}
