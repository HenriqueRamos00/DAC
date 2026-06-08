package com.ufpr.bantads.conta.infrastructure.cache.redis.model;

import java.math.BigDecimal;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@RedisHash("ms-conta:conta")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContaCache {

    public static String idForNumeroConta(String numeroConta) {
        return numeroConta;
    }

    @Id
    private String id;

    @Indexed
    private String numeroConta;

    @Indexed
    private String clienteCpf;

    @Indexed
    private String eventId;

    private BigDecimal saldo;

    /**
     * TTL em segundos.
     * Aqui o cache expira automaticamente depois de 60 segundos.
     */
    @Builder.Default
    private Long expiration = 60L;
}
