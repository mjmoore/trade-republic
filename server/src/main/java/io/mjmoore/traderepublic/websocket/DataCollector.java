package io.mjmoore.traderepublic.websocket;

import java.util.function.Consumer;

@FunctionalInterface
public interface DataCollector<T> extends Consumer<T> {
}
