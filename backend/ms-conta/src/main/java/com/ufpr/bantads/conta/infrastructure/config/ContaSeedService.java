package com.ufpr.bantads.conta.infrastructure.config;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ufpr.bantads.conta.domain.model.entity.ContaCommand;
import com.ufpr.bantads.conta.domain.model.entity.ContaQuery;
import com.ufpr.bantads.conta.domain.model.entity.DepositoCommand;
import com.ufpr.bantads.conta.domain.model.entity.MovimentacaoQuery;
import com.ufpr.bantads.conta.domain.model.entity.SaqueCommand;
import com.ufpr.bantads.conta.domain.model.entity.TransferenciaCommand;
import com.ufpr.bantads.conta.domain.model.enums.TipoMovimentacao;
import com.ufpr.bantads.conta.domain.repository.ContaCommandRepository;
import com.ufpr.bantads.conta.domain.repository.ContaQueryRepository;
import com.ufpr.bantads.conta.domain.repository.DepositoCommandRepository;
import com.ufpr.bantads.conta.domain.repository.MovimentacaoQueryRepository;
import com.ufpr.bantads.conta.domain.repository.SaqueCommandRepository;
import com.ufpr.bantads.conta.domain.repository.TransferenciaCommandRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ContaSeedService {

        private final JdbcTemplate jdbcTemplate;
        private final ContaCommandRepository contaCommandRepository;
        private final ContaQueryRepository contaQueryRepository;
        private final DepositoCommandRepository depositoCommandRepository;
        private final SaqueCommandRepository saqueCommandRepository;
        private final TransferenciaCommandRepository transferenciaCommandRepository;
        private final MovimentacaoQueryRepository movimentacaoQueryRepository;

        @EventListener(ApplicationReadyEvent.class)
        @Transactional
        public void seedOnStartup() {
                long count = contaCommandRepository.count();

                if (count == 0) {
                        applySeed();
                }
        }

        @Transactional
        public void reboot() {
                jdbcTemplate.execute("TRUNCATE TABLE conta_write.conta, conta_write.deposito, " +
                                "conta_write.saque, conta_write.transferencia RESTART IDENTITY CASCADE");

                jdbcTemplate.execute("TRUNCATE TABLE conta_read.conta_view, " +
                                "conta_read.movimentacao_view RESTART IDENTITY CASCADE");

                applySeed();
        }

        private void applySeed() {
                long contaCatharynaId = insertConta("12912861012", "1291", "2000-01-01 00:00:00",
                                800.00, 5000.00, "98574307084", "Catharyna", "Geniéve", "ger1@bantads.com.br");

                long contaCleudonioId = insertConta("09506382000", "0950", "1990-10-10 00:00:00",
                                -10000.00, 10000.00, "64065268052", "Cleudônio", "Godophredo", "ger2@bantads.com.br");

                long contaCatiannaId = insertConta("85733854057", "8573", "2012-12-12 00:00:00",
                                -1000.00, 1500.00, "23862179060", "Catianna", "Gyândula", "ger3@bantads.com.br");

                long contaCutardoId = insertConta("58872160006", "5887", "2022-02-22 00:00:00",
                                150000.00, 0.00, "98574307084", "Cutardo", "Geniéve", "ger1@bantads.com.br");

                long contaCoandryaId = insertConta("76179646090", "7617", "2025-01-01 00:00:00",
                                1500.00, 0.00, "64065268052", "Coândrya", "Godophredo", "ger2@bantads.com.br");

                insertDeposito(contaCatharynaId, "2020-01-01 10:00:00", 1000.00, "1291", "Catharyna");
                insertDeposito(contaCatharynaId, "2020-01-01 11:00:00", 900.00, "1291", "Catharyna");
                insertSaque(contaCatharynaId, "2020-01-01 12:00:00", 550.00, "1291", "Catharyna");
                insertSaque(contaCatharynaId, "2020-01-01 13:00:00", 350.00, "1291", "Catharyna");
                insertDeposito(contaCatharynaId, "2020-01-10 15:00:00", 2000.00, "1291", "Catharyna");
                insertSaque(contaCatharynaId, "2020-01-15 08:00:00", 500.00, "1291", "Catharyna");

                insertTransferencia(contaCatharynaId, contaCleudonioId, "2020-01-20 12:00:00",
                                1700.00, "1291", "Catharyna", "0950", "Cleudônio");

                insertDeposito(contaCleudonioId, "2025-01-01 12:00:00", 1000.00, "0950", "Cleudônio");
                insertDeposito(contaCleudonioId, "2025-02-01 10:00:00", 5000.00, "0950", "Cleudônio");
                insertSaque(contaCleudonioId, "2025-01-10 10:00:00", 200.00, "0950", "Cleudônio");
                insertDeposito(contaCleudonioId, "2025-05-02 10:00:00", 7000.00, "0950", "Cleudônio");

                insertDeposito(contaCatiannaId, "2025-05-05 10:00:00", 1000.00, "8573", "Catianna");
                insertSaque(contaCatiannaId, "2025-05-06 10:00:00", 2000.00, "8573", "Catianna");

                insertDeposito(contaCutardoId, "2025-06-01 10:00:00", 150000.00, "5887", "Cutardo");
                insertDeposito(contaCoandryaId, "2025-07-01 10:00:00", 1500.00, "7617", "Coândrya");

                alignSequences();
        }

        private long insertConta(
                        String cpf,
                        String num,
                        String data,
                        double saldo,
                        double limite,
                        String gerCpf,
                        String nomeCli,
                        String nomeGer,
                        String emailGer) {
                LocalDateTime dt = Timestamp.valueOf(data).toLocalDateTime();

                ContaCommand contaCmd = new ContaCommand(
                                null,
                                cpf,
                                num,
                                dt,
                                BigDecimal.valueOf(saldo),
                                BigDecimal.valueOf(limite),
                                gerCpf);

                ContaCommand contaSalva = contaCommandRepository.saveAndFlush(contaCmd);
                long id = contaSalva.getId();

                ContaQuery contaView = ContaQuery.builder()
                                .id(id)
                                .numeroConta(num)
                                .dataCriacao(dt)
                                .saldo(BigDecimal.valueOf(saldo))
                                .limite(BigDecimal.valueOf(limite))
                                .clienteNome(nomeCli)
                                .clienteCpf(cpf)
                                .gerenteCpf(gerCpf)
                                .gerenteNome(nomeGer)
                                .gerenteEmail(emailGer)
                                .build();

                contaQueryRepository.save(contaView);

                return id;
        }

        private void insertDeposito(
                        long contaId,
                        String data,
                        double valor,
                        String num,
                        String nome) {
                LocalDateTime dt = Timestamp.valueOf(data).toLocalDateTime();

                DepositoCommand deposito = DepositoCommand.builder()
                                .contaId(contaId)
                                .dataHora(dt)
                                .valor(BigDecimal.valueOf(valor))
                                .build();

                depositoCommandRepository.save(deposito);

                MovimentacaoQuery mov = MovimentacaoQuery.builder()
                                .eventId(UUID.randomUUID().toString())
                                .dataHora(dt)
                                .tipo(TipoMovimentacao.DEPOSITO)
                                .valor(BigDecimal.valueOf(valor))
                                .contaOrigemNumero(num)
                                .clienteOrigemNome(nome)
                                .contaDestinoNumero(null)
                                .clienteDestinoNome(null)
                                .build();

                movimentacaoQueryRepository.save(mov);
        }

        private void insertSaque(
                        long contaId,
                        String data,
                        double valor,
                        String num,
                        String nome) {
                LocalDateTime dt = Timestamp.valueOf(data).toLocalDateTime();

                SaqueCommand saque = SaqueCommand.builder()
                                .contaId(contaId)
                                .dataHora(dt)
                                .valor(BigDecimal.valueOf(valor))
                                .build();

                saqueCommandRepository.save(saque);

                MovimentacaoQuery mov = MovimentacaoQuery.builder()
                                .eventId(UUID.randomUUID().toString())
                                .dataHora(dt)
                                .tipo(TipoMovimentacao.SAQUE)
                                .valor(BigDecimal.valueOf(valor))
                                .contaOrigemNumero(num)
                                .clienteOrigemNome(nome)
                                .contaDestinoNumero(null)
                                .clienteDestinoNome(null)
                                .build();

                movimentacaoQueryRepository.save(mov);
        }

        private void insertTransferencia(
                        long oriId,
                        long destId,
                        String data,
                        double valor,
                        String numO,
                        String nomeO,
                        String numD,
                        String nomeD) {
                LocalDateTime dt = Timestamp.valueOf(data).toLocalDateTime();

                TransferenciaCommand t = TransferenciaCommand.builder()
                                .contaOrigemId(oriId)
                                .contaDestinoId(destId)
                                .dataHora(dt)
                                .valor(BigDecimal.valueOf(valor))
                                .build();

                transferenciaCommandRepository.save(t);

                MovimentacaoQuery mov = MovimentacaoQuery.builder()
                                .eventId(UUID.randomUUID().toString())
                                .dataHora(dt)
                                .tipo(TipoMovimentacao.TRANSFERENCIA)
                                .valor(BigDecimal.valueOf(valor))
                                .contaOrigemNumero(numO)
                                .clienteOrigemNome(nomeO)
                                .contaDestinoNumero(numD)
                                .clienteDestinoNome(nomeD)
                                .build();

                movimentacaoQueryRepository.save(mov);
        }

        private void alignSequences() {
                String[] tables = {
                                "conta_write.conta",
                                "conta_write.deposito",
                                "conta_write.saque",
                                "conta_write.transferencia",
                                "conta_read.movimentacao_view"
                };

                for (String table : tables) {
                        String seq = table + "_id_seq";

                        jdbcTemplate.execute(String.format(
                                        "SELECT setval('%s', COALESCE((SELECT MAX(id) FROM %s), 1), true)",
                                        seq,
                                        table));
                }
        }
}
