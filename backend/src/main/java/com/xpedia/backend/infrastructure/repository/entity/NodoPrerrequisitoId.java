package com.xpedia.backend.infrastructure.repository.entity;

import lombok.*;

import java.io.Serializable;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class NodoPrerrequisitoId implements Serializable {

    private static final long serialVersionUID = 1L;

    private UUID nodoId;

    private UUID prerrequisitoId;
}
