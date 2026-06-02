package com.ufpr.bantads.auth.infrastructure.config;

import org.springframework.stereotype.Service;

import com.ufpr.bantads.auth.domain.repository.UsuarioRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthDatabaseService {

	private final UsuarioRepository usuarioRepository;

	public void reboot() {
		usuarioRepository.deleteAll();
	}
}
