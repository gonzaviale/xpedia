package com.xpedia.backend.infrastructure.presentation.controller;

import com.xpedia.backend.domain.dto.usuario.RegistrarUsuarioRequest;
import com.xpedia.backend.domain.dto.usuario.UsuarioItem;
import com.xpedia.backend.domain.exception.DuplicateResourceException;
import com.xpedia.backend.domain.useCase.usuario.RegistrarUsuarioUseCase;
import com.xpedia.backend.infrastructure.presentation.exception.GlobalExceptionHandler;
import com.xpedia.backend.infrastructure.presentation.mapper.usuario.UsuarioPresentationMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static com.xpedia.backend.support.UsuarioTestData.CONTRASENIA;
import static com.xpedia.backend.support.UsuarioTestData.EMAIL;
import static com.xpedia.backend.support.UsuarioTestData.NOMBRE;
import static com.xpedia.backend.support.UsuarioTestData.USUARIO_ID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class UsuarioControllerTest {

    private static final String JSON = """
            {"nombre":"Persona","email":"persona@example.com","contrasenia":"Una contraseña segura"}
            """;

    private RegistrarUsuarioUseCase registrarUsuarioUseCase;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        registrarUsuarioUseCase = mock(RegistrarUsuarioUseCase.class);
        mockMvc = MockMvcBuilders.standaloneSetup(
                new UsuarioController(registrarUsuarioUseCase, new UsuarioPresentationMapper()))
                .setControllerAdvice(new GlobalExceptionHandler()).build();
    }

    @Test
    @DisplayName("Registro devuelve 201 y los campos públicos sin contraseña")
    void registrarShouldReturn201AndPublicUsuario() throws Exception {
        givenRegistrarShouldReturn201AndPublicUsuario();

        ResultActions result = registrar(JSON);

        thenRegistrarShouldReturn201AndPublicUsuario(result);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "{}",
            "{\"nombre\":\"Persona\",\"email\":\"invalido\",\"contrasenia\":\"Una contraseña segura\"}",
            "{\"nombre\":\"Persona\",\"email\":\"persona@example.com\",\"contrasenia\":\"corta\"}",
            "{\"nombre\":\"  \",\"email\":\"persona@example.com\",\"contrasenia\":\"Una contraseña segura\"}"
    })
    @DisplayName("Rechaza datos inválidos sin ejecutar el caso de uso")
    void registrarShouldReturn400WhenInputIsInvalid(String json) throws Exception {
        ResultActions result = registrar(json);

        result.andExpect(status().isBadRequest());
        verifyNoInteractions(registrarUsuarioUseCase);
    }

    @Test
    @DisplayName("Registro devuelve 409 al encontrar un email duplicado")
    void registrarShouldReturn409WhenEmailIsDuplicate() throws Exception {
        givenRegistrarShouldReturn409WhenEmailIsDuplicate();

        ResultActions result = registrar(JSON);

        thenRegistrarShouldReturn409WhenEmailIsDuplicate(result);
    }

    @Test
    @DisplayName("Rechaza JSON mal formado sin registrar ni filtrar su contenido")
    void registrarShouldReturn400WhenJsonIsMalformed() throws Exception {
        ResultActions result = registrar("{");

        thenRegistrarShouldReturn400WhenJsonIsMalformed(result);
    }

    // --- arrange ---
    private void givenRegistrarShouldReturn201AndPublicUsuario() throws Exception {
        when(registrarUsuarioUseCase.execute(any())).thenReturn(new UsuarioItem(USUARIO_ID, NOMBRE, EMAIL, "PERSONA"));
    }

    private void givenRegistrarShouldReturn409WhenEmailIsDuplicate() throws Exception {
        when(registrarUsuarioUseCase.execute(any())).thenThrow(new DuplicateResourceException("usuario",
                "email", EMAIL));
    }

    // --- act ---
    private ResultActions registrar(String json) throws Exception {
        return mockMvc.perform(post("/api/auth/registro").contentType(MediaType.APPLICATION_JSON).content(json));
    }

    // --- assert ---
    private void thenRegistrarShouldReturn201AndPublicUsuario(ResultActions result) throws Exception {
        result.andExpect(status().isCreated()).andExpect(header().string("Cache-Control", "no-store"))
                .andExpect(jsonPath("$.id").value(USUARIO_ID.toString()))
                .andExpect(jsonPath("$.nombre").value(NOMBRE)).andExpect(jsonPath("$.email").value(EMAIL))
                .andExpect(jsonPath("$.tipo").value("PERSONA"))
                .andExpect(jsonPath("$.hashContrasenia").doesNotExist())
                .andExpect(jsonPath("$.contrasenia").doesNotExist());
        ArgumentCaptor<RegistrarUsuarioRequest> captor = ArgumentCaptor.forClass(RegistrarUsuarioRequest.class);
        verify(registrarUsuarioUseCase).execute(captor.capture());
        assertThat(captor.getValue().nombre()).isEqualTo(NOMBRE);
        assertThat(captor.getValue().email()).isEqualTo(EMAIL);
        assertThat(captor.getValue().contrasenia()).isEqualTo(CONTRASENIA);
    }

    private void thenRegistrarShouldReturn409WhenEmailIsDuplicate(ResultActions result) throws Exception {
        result.andExpect(status().isConflict());
    }

    private void thenRegistrarShouldReturn400WhenJsonIsMalformed(ResultActions result) throws Exception {
        result.andExpect(status().isBadRequest()).andExpect(jsonPath("$.message").value("El cuerpo JSON no es válido"));
        verifyNoInteractions(registrarUsuarioUseCase);
    }
}
