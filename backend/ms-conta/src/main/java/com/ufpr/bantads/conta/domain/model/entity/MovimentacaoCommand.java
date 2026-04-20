package com.ufpr.bantads.conta.domain.model.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.CreationTimestamp;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
    name = "movimentacao",
    schema = "conta_write"
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MovimentacaoCommand {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "conta_id", nullable = false)
    private Long conta_id;

    @CreationTimestamp
    @Column(name = "data_hora", nullable = false, updatable = false)
    private LocalDateTime data_hora;

    @Column(name = "tipo", nullable = false, length = 15)
    private String tipo;

    @Column(name = "conta_destino_id")
    private Long conta_destino_id;

    @Column(name = "valor", nullable = false, precision = 15, scale = 2)
    private BigDecimal valor;

}
