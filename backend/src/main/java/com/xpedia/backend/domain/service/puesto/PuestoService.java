package com.xpedia.backend.domain.service.puesto;

import com.xpedia.backend.domain.exception.DuplicateResourceException;
import com.xpedia.backend.domain.exception.ResourceNotFoundException;
import com.xpedia.backend.domain.model.puesto.Puesto;
import com.xpedia.backend.domain.repository.puesto.PuestoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PuestoService {

    private final PuestoRepository puestoRepository;

    public Puesto crear(Puesto puesto) {
        puesto.setNombre(normalizar(puesto.getNombre()));
        assertNombreUnico(puesto.getOrganizacionId(), puesto.getNombre());
        return puestoRepository.save(puesto);
    }

    public Puesto actualizar(UUID id, Puesto cambios) {
        Puesto existente = findOrThrow(id);
        String nombre = normalizar(cambios.getNombre());
        assertNombreNoUsadoPorOtro(existente.getOrganizacionId(), nombre, id);
        existente.setNombre(nombre);
        return puestoRepository.save(existente);
    }

    public void eliminar(UUID id) {
        existsOrThrow(id);
        puestoRepository.deleteById(id);
    }

    public Puesto obtener(UUID id) {
        return findOrThrow(id);
    }

    public Page<Puesto> listar(UUID organizacionId, Pageable pageable) {
        return puestoRepository.findAll(organizacionId, pageable);
    }

    private Puesto findOrThrow(UUID id) {
        return puestoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("puesto", "id", id));
    }

    private void existsOrThrow(UUID id) {
        if (!puestoRepository.existsById(id)) {
            throw new ResourceNotFoundException("puesto", "id", id);
        }
    }

    private void assertNombreUnico(UUID organizacionId, String nombre) {
        if (puestoRepository.existsByOrganizacionIdAndNombre(organizacionId, nombre)) {
            throw new DuplicateResourceException("puesto", "nombre", nombre);
        }
    }

    private void assertNombreNoUsadoPorOtro(UUID organizacionId, String nombre, UUID id) {
        if (puestoRepository.existsByOrganizacionIdAndNombreAndIdNot(organizacionId, nombre, id)) {
            throw new DuplicateResourceException("puesto", "nombre", nombre);
        }
    }

    private String normalizar(String nombre) {
        return nombre.trim();
    }
}
