package com.alura.literAlura.principal;
import com.alura.literAlura.Model.*;
import com.alura.literAlura.service.ConsumoAPI;
import com.alura.literAlura.service.ConvierteDatos;
import com.alura.literAlura.service.LibroService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.InputMismatchException;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.stream.Collectors;

@Component
public class Principal {

    private Scanner teclado = new Scanner(System.in);
    private ConsumoAPI consumoAPI = new ConsumoAPI();
    private static final String URL_BASE = "https://gutendex.com/books/";
    private ConvierteDatos convierteDatos = new ConvierteDatos();

    @Autowired
    private LibroService libroService;

    public Principal(LibroService libroService) {
        this.libroService = libroService;
    }

    public Principal() {
    }

    public void mostrarElMenu() {
        var opcion = -1;
        while (opcion != 0) {
            String menuMensaje = """
                    -----------------------------------------------------
                    |                       Menú                        |
                    -----------------------------------------------------
                    |   1. Buscar libro por titulo                      |
                    |   2. Ver libros registrados                       |
                    |   3. Ver libros por idioma                        |
                    |   4. Ver autores registrados                      |
                    |   5. Ver autores vivos en un determinado año      |
                    |   0. Salir                                        |
                    -----------------------------------------------------  
                    """;
            System.out.println("\n"+menuMensaje);
            System.out.print("Elija una opción: ");

            try {
                opcion = teclado.nextInt();
                teclado.nextLine();

                switch (opcion) {
                    case 1:
                        buscarLibroPorTitulo();
                        break;
                    case 2:
                        VerLibrosRegistrados();
                        break;
                    case 3:
                        VerLibrosPorIdioma();
                        break;
                    case 4:
                        VerAutoresRegistrados();
                        break;
                    case 5:
                        VerAutoresVivosEnUnDeterminadoAño();
                        break;
                    case 0:
                        System.out.println("Cerrando la aplicación...");
                        System.exit(0);
                        break;
                    default:
                        System.out.println("Opción incorrecta");
                }
            } catch (InputMismatchException e) {
                System.out.println("Por favor, ingresa un número válido.");
                teclado.nextLine();
            }

        }
    }

    private Datos getDatosLibro() {
        System.out.print("Ingresa el nombre del libro que deseas buscar: ");
        String tituloLibro = teclado.nextLine();
        var json = consumoAPI.obtenerDatosAPI(URL_BASE+"?search="+tituloLibro.replace(" ", "+"));
        Datos datos = convierteDatos.obtenerDatos(json, Datos.class);
        return datos;
    }

    private void buscarLibroPorTitulo() {
        Datos datosEncontrados = getDatosLibro();
        Optional<DatosLibro> libroBuscado = datosEncontrados.resultados().stream()
                .findFirst();

        if (libroBuscado.isPresent()) {
            System.out.println("""
                     \n-------------------------------------------------
                                           Libro                                      
                     -------------------------------------------------""" +
                    "\n    Titulo: " + libroBuscado.get().titulo() +
                    "\n    Autor: " + libroBuscado.get().autor().stream()
                    .map(a -> a.nombre()).limit(1).collect(Collectors.joining()) +
                    "\n    Idioma: " + libroBuscado.get().idiomas() +
                    "\n    Número de descargas: " + libroBuscado.get().numeroDeDescargas() +
                    "\n-------------------------------------------------\n");
                    //.findFirst().map(Autor::getNombre).orElse("Desconocido");
            try {
                DatosLibro libroEncontrado = libroBuscado.get();
                Optional<Libro> libroExistente = libroService.verificarLibroExistenteEnBD(libroEncontrado.titulo());

                if (libroExistente.isPresent()) {
                    System.out.println("El libro '" + libroEncontrado.titulo() + "' ya se encuentra registrado en" +
                            " la base de datos.");
                } else {
                    Optional<Autor> autorExistente = libroService.consultarAutorExistenteEnBD(libroEncontrado.autor()
                            .stream().map(a -> a.nombre())
                            .collect(Collectors.joining()));

                    Autor autor = null;
                    if (autorExistente.isPresent()) {
                        System.out.println("El autor '" + autorExistente.get().getNombre() + "' ya esta registrado en" +
                                " la base de datos.");
                    } else {
                        autor = libroService.guardarAutor(libroEncontrado);
                    }

                    Libro libro = new Libro(libroEncontrado);
                    libro.setAutor(autor);
                    libro = libroService.guardarLibroConElAutor(libroEncontrado);

                    System.out.println("Se guardo el libro: " + libro.getTitulo());
                }
            } catch (Exception e) {
                System.out.println("Ha ocurrido un error: " + e.getMessage());
            }
        } else {
            System.out.println("No se encontró el libro...valida los datos ingresados e intenta nuevamente");
        }
    }

    private void VerLibrosRegistrados() {
        List<Libro> listaDeLibros = libroService.listarLibrosRegistrados();
        listaDeLibros.forEach(libro -> System.out.println("""
                     \n-------------------------------------------------
                                           Libro                                      
                     -------------------------------------------------""" +
                "\n    Titulo: " + libro.getTitulo() +
                "\n    Autor: " + libro.getAutor().getNombre() +
                "\n    Idioma: " + libro.getIdioma().getIdiomaEspanol() +
                "\n    Número de descargas: " + libro.getNumeroDeDescargas() +
                "\n-------------------------------------------------\n"));
    }

    private void VerLibrosPorIdioma() {
        String menuIdiomas = """
                    -------------------------------
                    |       Menú de Idiomas       |
                    -------------------------------
                    |       en - Inglés           |
                    |       es - Español          |
                    |       fr - Francés          |
                    |       pt - Portugués        |
                    -------------------------------
                """;
        System.out.println("\n" + menuIdiomas);
        System.out.print("Escribe el idioma que deseas buscar: ");
        String idiomaIngresado = teclado.nextLine().trim().toLowerCase();

        if (!Idioma.IdiomaValido(idiomaIngresado)) {
            System.out.println("Selecciona un idioma válido. Los idiomas disponibles son: es, en, fr, pt.");
            return;
        }

        Idioma idiomaSeleccionado = Idioma.fromString(idiomaIngresado);
        List<Libro> librosPorIdioma = libroService.listarLibrosPorIdiomas(idiomaSeleccionado);
        if (librosPorIdioma.isEmpty()) {
            System.out.println("No se encontraron libros en el idioma ingresado.");
            return;
        }
        librosPorIdioma.forEach(libro -> System.out.println("""
                \n-------------------------------------------------
                                      Libro                                     
                -------------------------------------------------""" +
                "\n    Titulo: " + libro.getTitulo() +
                "\n    Autor: " + libro.getAutor().getNombre() +
                "\n    Idioma: " + libro.getIdioma().getIdiomaEspanol() +
                "\n    Número de descargas: " + libro.getNumeroDeDescargas() +
                "\n-------------------------------------------------\n"));
    }
    private void VerAutoresRegistrados() {
        List<Autor> autoresRegistrados = libroService.listarAutoresRegistrados();
        if (autoresRegistrados.isEmpty()) {
            System.out.println("No hay autores registrados en la base de datos.");
        } else {
            autoresRegistrados.forEach(a -> System.out.println("""
                     \n-------------------------------------------------
                                           Autor                                      
                     -------------------------------------------------""" +
                    "\n    Nombre: " + a.getNombre() +
                    "\n    Fecha de nacimiento: " + a.getFechaDeNacimiento() +
                    "\n    Fecha de fallecimiento: " + a.getFechaDeFallecimiento() +
                    "\n    Libros: " + a.getLibros() +
                    "\n-------------------------------------------------\n"));
        }
    }

    private void VerAutoresVivosEnUnDeterminadoAño() {
        System.out.print("Ingrese el año para buscar autor(es) vivos en ese año: ");
        try {
            Integer anioIngresado = teclado.nextInt();
            teclado.nextLine();
            List<Autor> autoresPorAnio = libroService.listarAutoresPorAnio(anioIngresado);
            if (!autoresPorAnio.isEmpty()) {
                autoresPorAnio.forEach(a -> System.out.println("""
                     \n-------------------------------------------------
                                           Autor                                      
                     -------------------------------------------------""" +
                        "\n    Nombre: " + a.getNombre() +
                        "\n    Fecha de nacimiento: " + a.getFechaDeNacimiento() +
                        "\n    Fecha de fallecimiento: " + a.getFechaDeFallecimiento() +
                        "\n    Libros: " + a.getLibros() +
                        "\n-------------------------------------------------\n"));
            } else {
                System.out.println("No se encontraron autores vivos en la base de datos para el año ingresado.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Introduce un año válido.");
        } catch (InputMismatchException e) {
            System.out.println("Ingresa un año de 4 cifras.");
        } catch (Exception e) {
            System.out.println("Ocurrió un error: " + e.getMessage());
        }
    }
}
