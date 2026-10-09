package com.xpedia.backend.infrastructure.config;

import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.*;
import org.springframework.test.util.ReflectionTestUtils;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class CorsFilterTest {
    private final CorsFilter filter = new CorsFilter();
    @Test void origenPermitidoIncluyeHeadersYContinuaLaCadena() throws Exception {
        ReflectionTestUtils.setField(filter, "frontendUrl", "http://localhost:5173");
        var request = new MockHttpServletRequest("GET", "/api/rutas"); request.addHeader("Origin", "http://localhost:5173");
        var response = new MockHttpServletResponse(); var chain = mock(FilterChain.class);
        filter.doFilter(request, response, chain);
        assertThat(response.getHeader("Access-Control-Allow-Origin")).isEqualTo("http://localhost:5173");
        assertThat(response.getHeader("Access-Control-Allow-Credentials")).isEqualTo("true");
        assertThat(response.getHeader("Access-Control-Allow-Methods")).contains("GET", "OPTIONS");
        assertThat(response.getHeader("Access-Control-Allow-Headers")).isEqualTo("Authorization, Content-Type");
        assertThat(response.getHeader("Access-Control-Max-Age")).isEqualTo("86400");
        verify(chain).doFilter(request, response);
    }
    @Test void origenAjenoNoRecibeAutorizacionCors() throws Exception {
        ReflectionTestUtils.setField(filter, "frontendUrl", "http://localhost:5173");
        var request = new MockHttpServletRequest("GET", "/api/rutas"); request.addHeader("Origin", "http://example.org");
        var response = new MockHttpServletResponse(); var chain = mock(FilterChain.class);
        filter.doFilter(request, response, chain); assertThat(response.getHeader("Access-Control-Allow-Origin")).isNull();
        verify(chain).doFilter(request, response);
    }
    @Test void preflightTerminaSinInvocarControladores() throws Exception {
        var request = new MockHttpServletRequest("OPTIONS", "/api/rutas"); var response = new MockHttpServletResponse();
        var chain = mock(FilterChain.class); filter.doFilter(request, response, chain);
        assertThat(response.getStatus()).isEqualTo(200); verifyNoInteractions(chain);
    }
}
