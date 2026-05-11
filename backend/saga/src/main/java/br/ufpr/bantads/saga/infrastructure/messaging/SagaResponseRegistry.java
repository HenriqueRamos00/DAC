package br.ufpr.bantads.saga.infrastructure.messaging;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.springframework.stereotype.Component;

@Component
public class SagaResponseRegistry {

    private final ConcurrentMap<String, CompletableFuture<Object>> pending = new ConcurrentHashMap<>();

    public CompletableFuture<Object> register(String sagaId) {
        CompletableFuture<Object> future = new CompletableFuture<>();
        pending.put(sagaId, future);
        return future;
    }

    public void complete(String sagaId, Object payload) {
        CompletableFuture<Object> future = pending.remove(sagaId);
        if (future != null) {
            future.complete(payload);
        }
    }

    public void cancel(String sagaId) {
        CompletableFuture<Object> future = pending.remove(sagaId);
        if (future != null) {
            future.cancel(true);
        }
    }
}
