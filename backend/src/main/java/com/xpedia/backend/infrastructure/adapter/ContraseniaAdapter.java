package com.xpedia.backend.infrastructure.adapter;

import com.xpedia.backend.domain.port.ContraseniaPort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class ContraseniaAdapter implements ContraseniaPort {

    private final PasswordEncoder passwordEncoder;

    private final String hashFicticio;

    public ContraseniaAdapter(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
        this.hashFicticio = passwordEncoder.encode(UUID.randomUUID().toString());
    }

    @Override
    public String codificar(String contrasenia) {
        return passwordEncoder.encode(contrasenia);
    }

    @Override
    public boolean coincide(String contrasenia, String hash) {
        String candidato = hash == null ? hashFicticio : hash;
        boolean coincide = passwordEncoder.matches(contrasenia, candidato);
        return hash != null && coincide;
    }
}
