package com.ufpr.bantads.conta.domain.model.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

@Entity
@Table(
    name = "transferencia",
    schema = "conta_write"
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransferenciaCommand {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "conta_origem_id", nullable = false)
    private Long contaOrigemId;

    @Column(name = "conta_destino_id", nullable = false)
    private Long contaDestinoId;

    @CreationTimestamp
    @Column(name = "data_hora", nullable = false, updatable = false)
    private LocalDateTime dataHora;

    @Column(name = "valor", nullable = false, precision = 15, scale = 2)
    private BigDecimal valor;

}
