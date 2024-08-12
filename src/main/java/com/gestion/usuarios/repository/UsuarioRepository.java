package com.gestion.usuarios.repository;

import com.gestion.usuarios.entity.Usuario;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface UsuarioRepository extends PagingAndSortingRepository<Usuario,Long> {

    Usuario findById(Long id);

    void save(Usuario usuario);

    Object findAll();

    void deleteById(Long id);

}
