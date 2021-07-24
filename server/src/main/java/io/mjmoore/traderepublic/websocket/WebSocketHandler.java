package io.mjmoore.traderepublic.websocket;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.time.Instant;

@Slf4j
public class WebSocketHandler<T> extends TextWebSocketHandler {
    private final ObjectMapper mapper = new ObjectMapper();

    private final DataCollector<T> collector;
    private final TypeReference<T> type;

    public WebSocketHandler(final DataCollector<T> collector, final TypeReference<T> type) {
        this.collector = collector;
        this.type = type;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        log.info("Session established for " + type.getType().getTypeName());
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        final T data = mapper.readValue(message.getPayload(), this.type);
        collector.accept(data);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        log.info("Session closed for " + type.getType().getTypeName());
    }
}
