package com.ufpr.bantads.cliente.application.dto.event;

import com.ufpr.bantads.cliente.domain.model.Cliente;
import java.math.BigDecimal;

public record ClientePerfilAlteradoEvent(
    String sagaId,
    String cpf,
    String nome,
    String email,
    String telefone,
    BigDecimal salario,
    String cep,
    String logradouro,
    String cidade,
    String estado,
    String complemento,
    String numero,
    String nomeAnterior,
    String emailAnterior,
    String telefoneAnterior,
    BigDecimal salarioAnterior,
    String cepAnterior,
    String logradouroAnterior,
    String cidadeAnterior,
    String estadoAnterior,
    String complementoAnterior,
    String numeroAnterior
) {
    public static ClientePerfilAlteradoEvent fromEntity(String sagaId, Cliente cliente) {
        return fromEntity(sagaId, cliente, null);
    }

    public static ClientePerfilAlteradoEvent fromEntity(
        String sagaId,
        Cliente cliente,
        DadosPerfil dadosAnteriores
    ) {
        var endereco = cliente.getEndereco();

        return new ClientePerfilAlteradoEvent(
            sagaId,
            cliente.getCpf(),
            cliente.getNome(),
            cliente.getEmail(),
            cliente.getTelefone(),
            cliente.getSalario(),
            endereco.getCep(),
            endereco.getLogradouro(),
            endereco.getCidade(),
            endereco.getEstado(),
            endereco.getComplemento(),
            endereco.getNumero(),
            dadosAnteriores == null ? null : dadosAnteriores.nome(),
            dadosAnteriores == null ? null : dadosAnteriores.email(),
            dadosAnteriores == null ? null : dadosAnteriores.telefone(),
            dadosAnteriores == null ? null : dadosAnteriores.salario(),
            dadosAnteriores == null ? null : dadosAnteriores.cep(),
            dadosAnteriores == null ? null : dadosAnteriores.logradouro(),
            dadosAnteriores == null ? null : dadosAnteriores.cidade(),
            dadosAnteriores == null ? null : dadosAnteriores.estado(),
            dadosAnteriores == null ? null : dadosAnteriores.complemento(),
            dadosAnteriores == null ? null : dadosAnteriores.numero()
        );
    }

    public record DadosPerfil(
        String nome,
        String email,
        String telefone,
        BigDecimal salario,
        String cep,
        String logradouro,
        String cidade,
        String estado,
        String complemento,
        String numero
    ) {
        public static DadosPerfil fromEntity(Cliente cliente) {
            var endereco = cliente.getEndereco();

            return new DadosPerfil(
                cliente.getNome(),
                cliente.getEmail(),
                cliente.getTelefone(),
                cliente.getSalario(),
                endereco.getCep(),
                endereco.getLogradouro(),
                endereco.getCidade(),
                endereco.getEstado(),
                endereco.getComplemento(),
                endereco.getNumero()
            );
        }
    }
}
