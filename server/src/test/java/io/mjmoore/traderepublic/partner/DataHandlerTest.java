package io.mjmoore.traderepublic.partner;

import io.mjmoore.traderepublic.mappers.Mapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Flux;

import java.util.Optional;

import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DataHandlerTest {

    private final DataBufferFactory dataBufferFactory = new DefaultDataBufferFactory();
    private final String payload = "test";

    @Mock private DataService<String> service;
    @Mock private Mapper<String, String> mapper;
    @Mock private WebSocketSession session;

    private DataHandler<String, String> dataHandler;

    @BeforeEach
    public void setup() {
        this.dataHandler = new DataHandler<>(mapper, service);
    }

    @Test
    public void simplePayload() {
        final WebSocketMessage message = message();

        when(session.receive()).thenReturn(Flux.just(message));
        when(mapper.toDto(any())).thenReturn(Optional.of(payload));
        when(mapper.toModel(any())).thenAnswer(returnsFirstArg());

        dataHandler.handle(session).subscribe();

        verify(mapper, times(1)).toDto(payload);
        verify(mapper, times(1)).toModel(payload);
        verify(service, times(1)).accept(payload);
    }

    @Test
    public void malformedPayload() {

        final WebSocketMessage message = message();

        when(session.receive()).thenReturn(Flux.just(message));
        when(mapper.toDto(any())).thenReturn(Optional.empty());

        dataHandler.handle(session).subscribe();

        verify(mapper, times(1)).toDto(payload);
        verify(mapper, times(0)).toModel(any());
        verify(service, times(0)).accept(any());
    }

    private WebSocketMessage message() {
        final DataBuffer data = dataBufferFactory.wrap(payload.getBytes());
        return new WebSocketMessage(WebSocketMessage.Type.TEXT, data);
    }
}
