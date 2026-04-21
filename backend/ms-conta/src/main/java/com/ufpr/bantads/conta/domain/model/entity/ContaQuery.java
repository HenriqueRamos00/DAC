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
    name = "conta_view",
    schema = "conta_read"
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ContaQuery {

    @Id
    private Long id;

    @Column(name = "numero_conta", nullable = false, length = 20)
    private String numeroConta;

    @Column(name = "data_criacao", nullable = false)
    private LocalDateTime dataCriacao;

    @Column(name = "saldo", nullable = false, precision = 15, scale = 2)
    private BigDecimal saldo;

    @Column(name = "limite", nullable = false, precision = 15, scale = 2)
    private BigDecimal limite;

    @Column(name = "cliente_nome", nullable = false, length = 150)
    private String clienteNome;

    @Column(name = "cliente_cpf", nullable = false, length = 11)
    private String clienteCpf;

    @Column(name = "gerente_cpf", nullable = false, length = 11)
    private String gerenteCpf;

    @Column(name = "gerente_nome", nullable = false, length = 150)
    private String gerenteNome;

}
