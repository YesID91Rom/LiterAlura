package com.alura.literAlura.Repository;

import com.alura.literAlura.Model.Autor;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository

public interface AutorRepository extends JpaRepository<Autor, Long> {

    Optional<Autor> findByNombre(String nombre);

    @Query("SELECT a FROM Autor a WHERE a.fechaDeNacimiento <=:añoIngresado AND a.fechaDeFallecimiento > :añoIngresado")
    List<Autor> listarAutoresPorAño(Integer añoIngresado);

    @Query("SELECT a FROM Autor a WHERE a.nombre ILIKE %:nombre%")
    Optional<Autor> buscarAutorPorNombre(String nombre);
}

