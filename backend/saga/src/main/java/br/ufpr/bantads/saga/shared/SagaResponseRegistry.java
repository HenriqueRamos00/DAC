package br.ufpr.bantads.saga.shared;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.springframework.stereotype.Component;

import br.ufpr.bantads.saga.shared.dto.response.SagaResult;

@Component
public class SagaResponseRegistry {

    private final ConcurrentMap<String, CompletableFuture<SagaResult>> pending = new ConcurrentHashMap<>();

    public CompletableFuture<SagaResult> register(String sagaId) {
        CompletableFuture<SagaResult> future = new CompletableFuture<>();
        pending.put(sagaId, future);
        return future;
    }

    public void complete(String sagaId, SagaResult payload) {
        CompletableFuture<SagaResult> future = pending.remove(sagaId);
        if (future != null) {
            future.complete(payload);
        }
    }

    public void cancel(String sagaId) {
        CompletableFuture<SagaResult> future = pending.remove(sagaId);
        if (future != null) {
            future.cancel(true);
        }
    }
}