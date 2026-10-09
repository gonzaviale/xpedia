package com.xpedia.backend.infrastructure.repository.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "nodo_prerrequisito")
@IdClass(NodoPrerrequisitoId.class)
public class NodoPrerrequisitoEntity {

    @Id
    @Column(name = "nodo_id", nullable = false)
    private UUID nodoId;

    @Id
    @Column(name = "prerrequisito_id", nullable = false)
    private UUID prerrequisitoId;

    @Column(name = "creado_en", nullable = false)
    private OffsetDateTime creadoEn;
}
