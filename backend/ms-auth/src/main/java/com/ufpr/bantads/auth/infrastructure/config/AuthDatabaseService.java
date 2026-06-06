package com.ufpr.bantads.auth.infrastructure.config;

import java.util.List;

import org.springframework.stereotype.Service;

import com.ufpr.bantads.auth.domain.model.entity.Usuario;
import com.ufpr.bantads.auth.domain.model.enums.TipoUsuario;
import com.ufpr.bantads.auth.domain.repository.UsuarioRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthDatabaseService {

	private final UsuarioRepository usuarioRepository;

	public void reboot() {
		usuarioRepository.deleteAll();
		usuarioRepository.saveAll(seedUsuarios());
	}

	private List<Usuario> seedUsuarios() {
		return List.of(
				usuario("Catharyna", "12912861012", "cli1@bantads.com.br",
						"8PgqjzcWYmB93ZM7/UGNCw==:vBJG389YrpkR7zu08qCdrv/f/CmhH6x8rXoZHaohuYs=", TipoUsuario.CLIENTE),
				usuario("Cleudônio", "09506382000", "cli2@bantads.com.br",
						"r5eTmzlTp34gyz3tbZmuYw==:T/GfCwM++LnL+8Q8OWQphqMmINHC5jU1AYG/uyyVy6g=", TipoUsuario.CLIENTE),
				usuario("Catianna", "85733854057", "cli3@bantads.com.br",
						"pxId7AS2gh39oyx68B/4Xw==:Yv7Lx6wZb+VaH7TWqaL7Qu0hkeBBPsOJW+WSr/o2ddg=", TipoUsuario.CLIENTE),
				usuario("Cutardo", "58872160006", "cli4@bantads.com.br",
						"eSvH0lt9GThfGFhAPKPr7Q==:QgAj+eP9hK1TsgxpJ+m7r+clTT+f+DFLMr8BR7y9LE8=", TipoUsuario.CLIENTE),
				usuario("Coândrya", "76179646090", "cli5@bantads.com.br",
						"QUfF70Y+MAKZUKvxgA61xQ==:nwkP9NDyG7UqTmNe9UvmE6hSYNwmVuEjlZ6Dpht3Hi8=", TipoUsuario.CLIENTE),
				usuario("Geniéve", "98574307084", "ger1@bantads.com.br",
						"6UIKBNtuEETEpIBsAf9Zsg==:n/5x5sH24XVlpPjgdDzbRiNbyUlnKTZOHftVbNd2fuA=", TipoUsuario.GERENTE),
				usuario("Godophredo", "64065268052", "ger2@bantads.com.br",
						"nOf79j9LG7OySCFQOu87SA==:shUDkH74FeKaswXn2PgGAvZt3IDkwcmqt1OIpmZv4vQ=", TipoUsuario.GERENTE),
				usuario("Gyândula", "23862179060", "ger3@bantads.com.br",
						"bsjcXsmhpPL8iefXGu6OAw==:JScd0B+HdhKGcT/3wAc5mu/wPmf+OLe6sQD+uvoN7lc=", TipoUsuario.GERENTE),
				usuario("Adamântio", "40501740066", "adm1@bantads.com.br",
						"C5VDUtN+Utlel9SPPX0aTw==:IzDCdPsYZRsLsre40EARmc5aK3IUhO/UFQZfyncYRL4=", TipoUsuario.ADMINISTRADOR));
	}

	private Usuario usuario(String nome, String cpf, String email, String senha, TipoUsuario tipoUsuario) {
		Usuario usuario = new Usuario();
		usuario.setNome(nome);
		usuario.setCpf(cpf);
		usuario.setEmail(email);
		usuario.setSenha(senha);
		usuario.setTipoUsuario(tipoUsuario);
		return usuario;
	}
}
