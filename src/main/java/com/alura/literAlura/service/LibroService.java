package com.alura.literAlura.service;
import com.alura.literAlura.Model.*;
import com.alura.literAlura.Repository.AutorRepository;
import com.alura.literAlura.Repository.LibroRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class LibroService {

    @Autowired
    private LibroRepository libroRepository;
    @Autowired
    private AutorRepository autorRepository;

    public Optional<Libro> verificarLibroExistenteEnBD(String titulo) {
        return libroRepository.findByTitulo(titulo);
    }

    public Optional<Autor> consultarAutorExistenteEnBD(String nombre) {
        return autorRepository.buscarAutorPorNombre(nombre);
    }

    public Autor guardarAutor(DatosLibro datosLibro) {
        //Asumiremos que el libro solo tiene un autor
        DatosAutor datosAutor = datosLibro.autor().get(0);

        Optional<Autor> autorExistente = consultarAutorExistenteEnBD(datosAutor.nombre());
        if (autorExistente.isPresent()) {
            return autorExistente.get();
        } else {
            Autor autor = new Autor(datosAutor);
            return autorRepository.save(autor);
        }
    }

    public Libro guardarLibroConElAutor(DatosLibro datosLibro) {
        Autor autor = guardarAutor(datosLibro);

        Libro libro = new Libro(datosLibro);
        libro.setAutor(autor);

        return libroRepository.save(libro);
    }

    public List<Libro> listarLibrosRegistrados() {
        return libroRepository.findAll();
    }

    public List<Libro> listarLibrosPorIdiomas(Idioma idiomaSeleccionado) {
        return libroRepository.listarLibrosPorIdioma(idiomaSeleccionado);
    }

    public List<Autor> listarAutoresRegistrados() {
        return autorRepository.findAll();
    }

    public List<Autor> listarAutoresPorAnio(Integer anioIngresado) {
        return autorRepository.listarAutoresPorAño(anioIngresado);
    }
}