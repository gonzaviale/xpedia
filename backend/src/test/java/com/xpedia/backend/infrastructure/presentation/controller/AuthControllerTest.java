package com.xpedia.backend.infrastructure.presentation.controller;

import com.xpedia.backend.domain.dto.auth.IniciarSesionRequest;
import com.xpedia.backend.domain.dto.auth.ObtenerUsuarioActualRequest;
import com.xpedia.backend.domain.dto.usuario.UsuarioItem;
import com.xpedia.backend.domain.exception.CredencialesInvalidasException;
import com.xpedia.backend.domain.useCase.auth.IniciarSesionUseCase;
import com.xpedia.backend.domain.useCase.auth.ObtenerUsuarioActualUseCase;
import com.xpedia.backend.infrastructure.presentation.exception.GlobalExceptionHandler;
import com.xpedia.backend.infrastructure.presentation.mapper.auth.AuthPresentationMapper;
import com.xpedia.backend.infrastructure.security.SesionAdapter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.web.csrf.DefaultCsrfToken;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static com.xpedia.backend.support.UsuarioTestData.CONTRASENIA;
import static com.xpedia.backend.support.UsuarioTestData.EMAIL;
import static com.xpedia.backend.support.UsuarioTestData.NOMBRE;
import static com.xpedia.backend.support.UsuarioTestData.USUARIO_ID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AuthControllerTest {

    private static final String JSON = """
            {"email":"persona@example.com","contrasenia":"Una contraseña segura"}
            """;

    private IniciarSesionUseCase iniciarSesionUseCase;

    private ObtenerUsuarioActualUseCase obtenerUsuarioActualUseCase;

    private SesionAdapter sesionAdapter;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        iniciarSesionUseCase = mock(IniciarSesionUseCase.class);
        obtenerUsuarioActualUseCase = mock(ObtenerUsuarioActualUseCase.class);
        sesionAdapter = mock(SesionAdapter.class);
        mockMvc = MockMvcBuilders.standaloneSetup(new AuthController(
                iniciarSesionUseCase, obtenerUsuarioActualUseCase, new AuthPresentationMapper(), sesionAdapter))
                .setControllerAdvice(new GlobalExceptionHandler()).build();
    }

    @Test
    @DisplayName("Login devuelve datos públicos y delega el establecimiento de sesión")
    void iniciarShouldReturnUsuarioAndStartSession() throws Exception {
        givenIniciarShouldReturnUsuarioAndStartSession();

        ResultActions result = mockMvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON)
                .content(JSON));

        thenIniciarShouldReturnUsuarioAndStartSession(result);
    }

    @Test
    @DisplayName("Login devuelve 401 genérico y no establece sesión si las credenciales fallan")
    void iniciarShouldReturn401WhenCredentialsFail() throws Exception {
        givenIniciarShouldReturn401WhenCredentialsFail();

        ResultActions result = mockMvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON)
                .content(JSON));

        thenIniciarShouldReturn401WhenCredentialsFail(result);
    }

    @Test
    @DisplayName("Login rechaza cuerpo inválido antes de autenticar")
    void iniciarShouldReturn400WhenInputIsInvalid() throws Exception {
        ResultActions result = mockMvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON)
                .content("{}"));

        thenIniciarShouldReturn400WhenInputIsInvalid(result);
    }

    @Test
    @DisplayName("Usuario actual usa el identificador autenticado y devuelve datos públicos")
    void actualShouldUseAuthenticatedIdentity() throws Exception {
        givenActualShouldUseAuthenticatedIdentity();

        ResultActions result = mockMvc.perform(get("/api/auth/actual")
                .principal(UsernamePasswordAuthenticationToken.unauthenticated(USUARIO_ID.toString(), null)));

        thenActualShouldUseAuthenticatedIdentity(result);
    }

    @Test
    @DisplayName("Logout invalida la sesión y responde 204")
    void cerrarShouldInvalidateSessionAndReturn204() throws Exception {
        ResultActions result = mockMvc.perform(post("/api/auth/logout"));

        thenCerrarShouldInvalidateSessionAndReturn204(result);
    }

    @Test
    @DisplayName("CSRF entrega el token y el nombre del header sin caché")
    void csrfShouldReturnTokenAndHeaderName() throws Exception {
        ResultActions result = mockMvc.perform(get("/api/auth/csrf")
                .requestAttr("_csrf", new DefaultCsrfToken("X-CSRF-TOKEN", "_csrf", "token")));

        thenCsrfShouldReturnTokenAndHeaderName(result);
    }

    // --- arrange ---
    private void givenIniciarShouldReturnUsuarioAndStartSession() throws Exception {
        when(iniciarSesionUseCase.execute(any())).thenReturn(new UsuarioItem(USUARIO_ID, NOMBRE, EMAIL, "PERSONA"));
    }

    private void givenIniciarShouldReturn401WhenCredentialsFail() throws Exception {
        when(iniciarSesionUseCase.execute(any())).thenThrow(new CredencialesInvalidasException());
    }

    private void givenActualShouldUseAuthenticatedIdentity() throws Exception {
        when(obtenerUsuarioActualUseCase.execute(any())).thenReturn(new UsuarioItem(USUARIO_ID, NOMBRE, EMAIL,
                "PERSONA"));
    }

    // --- assert ---
    private void thenIniciarShouldReturnUsuarioAndStartSession(ResultActions result) throws Exception {
        result.andExpect(status().isOk()).andExpect(header().string("Cache-Control", "no-store"))
                .andExpect(jsonPath("$.id").value(USUARIO_ID.toString()))
                .andExpect(jsonPath("$.nombre").value(NOMBRE)).andExpect(jsonPath("$.email").value(EMAIL))
                .andExpect(jsonPath("$.tipo").value("PERSONA"));
        verify(sesionAdapter).iniciar(eq(USUARIO_ID), eq("PERSONA"), any(), any());
        ArgumentCaptor<IniciarSesionRequest> captor = ArgumentCaptor.forClass(IniciarSesionRequest.class);
        verify(iniciarSesionUseCase).execute(captor.capture());
        assertThat(captor.getValue().email()).isEqualTo(EMAIL);
        assertThat(captor.getValue().contrasenia()).isEqualTo(CONTRASENIA);
    }

    private void thenIniciarShouldReturn401WhenCredentialsFail(ResultActions result) throws Exception {
        result.andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("No se pudo autenticar el acceso"))
                .andExpect(header().string("Cache-Control", "no-store"));
        verifyNoInteractions(sesionAdapter);
    }

    private void thenIniciarShouldReturn400WhenInputIsInvalid(ResultActions result) throws Exception {
        result.andExpect(status().isBadRequest());
        verifyNoInteractions(iniciarSesionUseCase, sesionAdapter);
    }

    private void thenActualShouldUseAuthenticatedIdentity(ResultActions result) throws Exception {
        result.andExpect(status().isOk()).andExpect(jsonPath("$.id").value(USUARIO_ID.toString()));
        verify(obtenerUsuarioActualUseCase).execute(new ObtenerUsuarioActualRequest(USUARIO_ID));
    }

    private void thenCerrarShouldInvalidateSessionAndReturn204(ResultActions result) throws Exception {
        result.andExpect(status().isNoContent());
        verify(sesionAdapter).cerrar(any(), any());
    }

    private void thenCsrfShouldReturnTokenAndHeaderName(ResultActions result) throws Exception {
        result.andExpect(status().isOk()).andExpect(jsonPath("$.token").value("token"))
                .andExpect(jsonPath("$.headerName").value("X-CSRF-TOKEN"))
                .andExpect(header().string("Cache-Control", "no-store"));
    }
}
