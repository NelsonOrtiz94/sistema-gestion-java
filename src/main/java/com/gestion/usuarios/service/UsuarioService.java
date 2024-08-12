package com.gestion.usuarios.service;

import com.gestion.usuarios.entity.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface UsuarioService {

    public List<Usuario> findAll();

    public Page<Usuario> findAll(Pageable pageable);

    public void save(Usuario usuario);

    public Object findOne(Long id);

    public void delete(Long id);

}
