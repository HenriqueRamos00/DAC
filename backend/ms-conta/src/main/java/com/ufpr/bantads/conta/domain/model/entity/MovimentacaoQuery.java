package com.ufpr.bantads.conta.domain.model.entity;

import com.ufpr.bantads.conta.domain.model.enums.TipoMovimentacao;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
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
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MovimentacaoQuery {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "event_id", nullable = false, unique = true, length = 36)
    private String eventId;

    @Column(name = "data_hora", nullable = false)
    private LocalDateTime dataHora;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", nullable = false, length = 15)
    private TipoMovimentacao tipo;

    @Column(name = "valor", nullable = false, precision = 15, scale = 2)
    private BigDecimal valor;

    @Column(name = "conta_origem_numero", nullable = false, length = 20)
    private String contaOrigemNumero;

    @Column(name = "cliente_origem_nome", nullable = false, length = 150)
    private String clienteOrigemNome;

    @Column(name = "conta_destino_numero", length = 20)
    private String contaDestinoNumero;

    @Column(name = "cliente_destino_nome", length = 150)
    private String clienteDestinoNome;

}
