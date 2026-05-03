package com.ufpr.bantads.cliente.infrastructure.config;

import com.ufpr.bantads.cliente.domain.model.Cliente;
import com.ufpr.bantads.cliente.domain.model.Endereco;
import com.ufpr.bantads.cliente.domain.model.StatusCliente;
import com.ufpr.bantads.cliente.domain.repository.ClienteRepository;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ClienteSeedService {

    private final JdbcTemplate jdbcTemplate;
    private final ClienteRepository clienteRepository;

    @EventListener(ApplicationReadyEvent.class)
    @Transactional
    public void seedOnStartup() {
        applySeedIfEmpty();
    }

    @Transactional
    public void reboot() {
        jdbcTemplate.execute("TRUNCATE TABLE schema_cliente.cliente, schema_cliente.endereco RESTART IDENTITY CASCADE");
        applySeed();
    }

    private void applySeedIfEmpty() {
        if (clienteRepository.count() == 0) {
            jdbcTemplate.execute("TRUNCATE TABLE schema_cliente.cliente, schema_cliente.endereco RESTART IDENTITY CASCADE");
            applySeed();
        } else {
            alignSequences();
        }
    }

    private void applySeed() {
        clienteRepository.saveAll(List.of(
            clienteSeed(
                "Catharyna",
                "cli1@bantads.com.br",
                "12912861012",
                "10000.00",
                LocalDateTime.of(2000, 1, 1, 0, 0),
                enderecoSeed("Rua XV de Novembro", "100", "Apto 101", "80020310")
            ),
            clienteSeed(
                "Cleudônio",
                "cli2@bantads.com.br",
                "09506382000",
                "20000.00",
                LocalDateTime.of(1990, 10, 10, 0, 0),
                enderecoSeed("Av. Sete de Setembro", "200", null, "80040120")
            ),
            clienteSeed(
                "Catianna",
                "cli3@bantads.com.br",
                "85733854057",
                "3000.00",
                LocalDateTime.of(2012, 12, 12, 0, 0),
                enderecoSeed("Rua Marechal Deodoro", "300", "Sala 5", "80010010")
            ),
            clienteSeed(
                "Cutardo",
                "cli4@bantads.com.br",
                "58872160006",
                "500.00",
                LocalDateTime.of(2022, 2, 22, 0, 0),
                enderecoSeed("Rua Barão do Rio Branco", "400", null, "80010180")
            ),
            clienteSeed(
                "Coândrya",
                "cli5@bantads.com.br",
                "76179646090",
                "1500.00",
                LocalDateTime.of(2025, 1, 1, 0, 0),
                enderecoSeed("Rua Comendador Araújo", "500", "Bloco B", "80420000")
            )
        ));
    }

    private void alignSequences() {
        jdbcTemplate.execute("""
            SELECT setval(
                'schema_cliente.endereco_id_seq',
                COALESCE((SELECT MAX(id) FROM schema_cliente.endereco), 1),
                true
            )
            """);

        jdbcTemplate.execute("""
            SELECT setval(
                'schema_cliente.cliente_id_seq',
                COALESCE((SELECT MAX(id) FROM schema_cliente.cliente), 1),
                true
            )
            """);
    }

    private Cliente clienteSeed(
        String nome,
        String email,
        String cpf,
        String salario,
        LocalDateTime dataAprovacao,
        Endereco endereco
    ) {
        Cliente cliente = new Cliente();
        cliente.setNome(nome);
        cliente.setEmail(email);
        cliente.setCpf(cpf);
        cliente.setTelefone(null);
        cliente.setSalario(new BigDecimal(salario));
        cliente.setEndereco(endereco);
        cliente.setStatus(StatusCliente.APROVADO);
        cliente.setDataAprovacao(dataAprovacao);
        return cliente;
    }

    private Endereco enderecoSeed(
        String logradouro,
        String numero,
        String complemento,
        String cep
    ) {
        Endereco endereco = new Endereco();
        endereco.setLogradouro(logradouro);
        endereco.setNumero(numero);
        endereco.setComplemento(complemento);
        endereco.setCep(cep);
        endereco.setCidade("Curitiba");
        endereco.setEstado("PR");
        return endereco;
    }
}
