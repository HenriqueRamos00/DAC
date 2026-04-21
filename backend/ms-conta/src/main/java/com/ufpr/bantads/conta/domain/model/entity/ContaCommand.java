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
    name = "conta",
    schema = "conta_write"
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ContaCommand {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "cliente_cpf", nullable = false, length = 11)
    private String clienteCpf;

    @Column(name = "numero_conta", nullable = false, length = 20, unique = true)
    private String numeroConta;

    @CreationTimestamp
    @Column(name = "data_criacao", nullable = false, updatable = false)
    private LocalDateTime dataCriacao;

    @Column(name = "saldo", nullable = false, precision = 15, scale = 2)
    private BigDecimal saldo;

    @Column(name = "limite", nullable = false, precision = 15, scale = 2)
    private BigDecimal limite;

    @Column(name = "gerente_cpf", nullable = false, length = 11)
    private String gerenteCpf;

}
