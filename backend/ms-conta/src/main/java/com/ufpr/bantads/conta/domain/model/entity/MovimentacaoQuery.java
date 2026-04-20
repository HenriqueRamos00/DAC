package com.ufpr.bantads.conta.domain.model.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
    name = "movimentacao_view",
    schema = "conta_read"
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MovimentacaoQuery {

    @Id
    private Long id;

    @Column(name = "data_hora", nullable = false)
    private LocalDateTime data_hora;

    @Column(name = "tipo", nullable = false, length = 15)
    private String tipo;

    @Column(name = "valor", nullable = false, precision = 15, scale = 2)
    private BigDecimal valor;

    @Column(name = "conta_origem_numero", nullable = false, length = 20)
    private String conta_origem_numero;

    @Column(name = "cliente_origem_nome", nullable = false, length = 150)
    private String cliente_origem_nome;

    @Column(name = "conta_destino_numero", length = 20)
    private String conta_destino_numero;

    @Column(name = "cliente_destino_nome", length = 150)
    private String cliente_destino_nome;

}
