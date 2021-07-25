package io.mjmoore.traderepublic.websocket;

import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.client.WebSocketClient;
import reactor.core.publisher.Mono;

import java.net.URI;

public class ReactiveWebSocketClient implements WebSocketClient {

    @Override
    public Mono<Void> execute(URI url, WebSocketHandler handler) {
        return null;
    }

    @Override
    public Mono<Void> execute(URI url, HttpHeaders headers, WebSocketHandler handler) {
        return null;
    }
}
