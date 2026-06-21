package com.movsapp.backend.service;

import com.movsapp.backend.dto.*;
import com.movsapp.backend.entity.*;
import com.movsapp.backend.exception.*;
import com.movsapp.backend.repository.SuscripcionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.List;

@Service @RequiredArgsConstructor
public class SuscripcionService {
    private final SuscripcionRepository repository;
    private final UsuarioService usuarios;
    private final PlanService planes;
    @Transactional(readOnly=true) public List<SuscripcionResponse> listar(){ return repository.findAll().stream().map(this::response).toList(); }
    @Transactional(readOnly=true) public SuscripcionResponse obtener(Long id){ return response(entidad(id)); }
    @Transactional(readOnly=true) public SuscripcionResponse porUsuario(Long usuarioId){ return response(repository.findByUsuarioId(usuarioId).orElseThrow(() -> new RecursoNoEncontradoException("Suscripción no encontrada para el usuario."))); }
    @Transactional public SuscripcionResponse crear(SuscripcionRequest r){
        if(repository.existsByUsuarioId(r.usuarioId())) throw new ConflictoException("El usuario ya tiene una suscripción.");
        Suscripcion s=new Suscripcion(); copiar(r,s); return response(repository.save(s));
    }
    @Transactional public SuscripcionResponse actualizar(Long id,SuscripcionRequest r){
        Suscripcion s=entidad(id);
        repository.findByUsuarioId(r.usuarioId()).filter(x -> !x.getId().equals(id)).ifPresent(x -> { throw new ConflictoException("El usuario ya tiene una suscripción."); });
        copiar(r,s); return response(s);
    }
    @Transactional public void eliminar(Long id){ repository.delete(entidad(id)); }
    private Suscripcion entidad(Long id){ return repository.findById(id).orElseThrow(() -> new RecursoNoEncontradoException("Suscripción no encontrada.")); }
    private void copiar(SuscripcionRequest r,Suscripcion s){
        Usuario usuario=usuarios.entidad(r.usuarioId()); Plan plan=planes.entidad(r.planId());
        LocalDate inicio=r.fechaInicio()==null?LocalDate.now():r.fechaInicio();
        s.setUsuario(usuario); s.setPlan(plan); s.setFechaInicio(inicio); s.setFechaExpiracion(inicio.plusDays(plan.getDuracionDias()));
        s.setEstado(r.estado()==null?EstadoSuscripcion.ACTIVA:r.estado());
    }
    private SuscripcionResponse response(Suscripcion s){
        EstadoSuscripcion estado=s.getEstado()==EstadoSuscripcion.ACTIVA && s.getFechaExpiracion().isBefore(LocalDate.now())?EstadoSuscripcion.VENCIDA:s.getEstado();
        return new SuscripcionResponse(s.getId(),s.getUsuario().getId(),s.getPlan().getId(),s.getPlan().getNombre(),s.getFechaInicio(),s.getFechaExpiracion(),estado);
    }
}
