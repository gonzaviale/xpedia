package com.xpedia.backend.infrastructure.repository.jpaRepository.interfaces;

import com.xpedia.backend.infrastructure.repository.entity.UsuarioEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface IUsuarioJpaRepository extends JpaRepository<UsuarioEntity, UUID> {

    /** Conserva compatibilidad con emails anteriores que contienen mayúsculas o espacios. */
    @Query("SELECT u FROM UsuarioEntity u WHERE LOWER(TRIM(u.email)) = :email")
    Optional<UsuarioEntity> findByEmailNormalizado(@Param("email") String email);
}
