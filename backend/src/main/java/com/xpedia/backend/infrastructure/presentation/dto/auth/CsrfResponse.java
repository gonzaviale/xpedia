package com.xpedia.backend.infrastructure.presentation.dto.auth;

public record CsrfResponse(String token, String headerName) {
}
