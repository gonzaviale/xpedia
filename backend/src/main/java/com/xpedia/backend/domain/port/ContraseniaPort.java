package com.xpedia.backend.domain.port;

public interface ContraseniaPort {

    String codificar(String contrasenia);

    boolean coincide(String contrasenia, String hash);
}
