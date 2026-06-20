package com.movsapp.backend.service;

import com.movsapp.backend.dto.*;
import com.movsapp.backend.entity.Plan;
import com.movsapp.backend.exception.*;
import com.movsapp.backend.repository.PlanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;

@Service @RequiredArgsConstructor
public class PlanService {
    private final PlanRepository repository;
    @Transactional(readOnly=true) public List<PlanResponse> listar() { return repository.findAll().stream().map(this::response).toList(); }
    @Transactional(readOnly=true) public PlanResponse obtener(Long id) { return response(entidad(id)); }
    @Transactional public PlanResponse crear(PlanRequest r) {
        validarCodigo(r.codigo(), null);
        Plan p = new Plan(); copiar(r, p); return response(repository.save(p));
    }
    @Transactional public PlanResponse actualizar(Long id, PlanRequest r) {
        Plan p = entidad(id); validarCodigo(r.codigo(), id); copiar(r, p); return response(p);
    }
    @Transactional public void eliminar(Long id) { repository.delete(entidad(id)); repository.flush(); }
    public Plan entidad(Long id) { return repository.findById(id).orElseThrow(() -> new RecursoNoEncontradoException("Plan no encontrado.")); }
    private void validarCodigo(String codigo, Long id) {
        boolean existe = id == null ? repository.existsByCodigoIgnoreCase(codigo) : repository.existsByCodigoIgnoreCaseAndIdNot(codigo, id);
        if (existe) throw new ConflictoException("El código del plan ya existe.");
    }
    private void copiar(PlanRequest r, Plan p) {
        p.setCodigo(r.codigo().trim().toLowerCase()); p.setNombre(r.nombre().trim()); p.setPrecio(r.precio());
        p.setDuracionDias(r.duracionDias()); p.setBeneficios(new ArrayList<>(r.beneficios()));
    }
    public PlanResponse response(Plan p) { return new PlanResponse(p.getId(), p.getCodigo(), p.getNombre(), p.getPrecio(), p.getDuracionDias(), List.copyOf(p.getBeneficios())); }
}
