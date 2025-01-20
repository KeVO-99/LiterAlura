package com.alura.literalura.main;

import com.alura.literalura.controller.ApiClient;
import com.alura.literalura.dto.AutorDTO;
import com.alura.literalura.dto.LibroDTO;
import com.alura.literalura.dto.SolicitudLibros;
import com.alura.literalura.model.Autor;
import com.alura.literalura.model.Libro;
import com.alura.literalura.service.AutorService;
import com.alura.literalura.service.ConvierteDatos;
import com.alura.literalura.service.LibroService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.stream.Collectors;

@Component
public class Menu {
    @Autowired
    private ApiClient apiClient;

    @Autowired
    private AutorService autorService;

    @Autowired
    private LibroService libroService;

    @Autowired
    private ConvierteDatos convierteDatos;

    private static final String BASE_URL = "https://gutendex.com/books/";

    public void mostrarMenu() {
        Scanner scanner = new Scanner(System.in);
        int opcionUsuario;

        do {
            System.out.println("-----------------------------------------------");
            System.out.println("Elija una opción a través de su número");
            System.out.println("1 - Buscar libro por título");
            System.out.println("2 - Listar libros registrados");
            System.out.println("3 - Listar autores registrados");
            System.out.println("4 - Listar autores vivos en un determinado año");
            System.out.println("5 - Listar libros por idioma");
            System.out.println("0 - Salir");

            System.out.print("Seleccione una opción: ");
            opcionUsuario = scanner.nextInt();
            scanner.nextLine();

            switch (opcionUsuario) {
                case 1:
                    System.out.print("Ingrese el título del libro: ");
                    String tituloLibro = scanner.nextLine();
                    try {
                        String encodedTitulo = URLEncoder.encode(tituloLibro, StandardCharsets.UTF_8);
                        String json = apiClient.obtenerDatos(BASE_URL + "?search=" + encodedTitulo);
                        SolicitudLibros solicitudLibros = convierteDatos.obtenerDatos(json, SolicitudLibros.class);
                        List<LibroDTO> librosDTO = solicitudLibros.getLibros();
                        if (librosDTO.isEmpty()) {
                            System.out.println("Libro no encontrado en la API");
                        } else {
                            boolean libroRegistrado = false;
                            for (LibroDTO libroDTO : librosDTO) {
                                if (libroDTO.getTitulo().equalsIgnoreCase(tituloLibro)) {
                                    Optional<Libro> libroExistente = libroService.obtenerLibroPorTitulo(tituloLibro);
                                    if (libroExistente.isPresent()) {
                                        System.out.println("Detalle: Clave (titulo)=(" + tituloLibro + ") ya existe");
                                        System.out.println("No se puede registrar el mismo libro más de una vez");
                                        libroRegistrado = true;
                                        break;
                                    } else {
                                        Libro libro = new Libro();
                                        libro.setTitulo(libroDTO.getTitulo());
                                        libro.setIdioma(libroDTO.getIdiomas().get(0));
                                        libro.setNumeroDescargas(libroDTO.getNumeroDeDescargas());

                                        AutorDTO primerAutorDTO = libroDTO.getAutores().get(0);
                                        Autor autor = autorService.obtenerAutorPorNombre(primerAutorDTO.getNombre())
                                                .orElseGet(() -> {
                                                    Autor nuevoAutor = new Autor();
                                                    nuevoAutor.setNombre(primerAutorDTO.getNombre());
                                                    nuevoAutor.setAñoNacimiento(primerAutorDTO.getAñoNacimiento());
                                                    nuevoAutor.setAñoFallecimiento(primerAutorDTO.getAñoFallecimiento());
                                                    return autorService.crearAutor(nuevoAutor);
                                                });

                                        libro.setAutor(autor);

                                        libroService.crearLibro(libro);
                                        System.out.println("Libro registrado: " + libro.getTitulo());
                                        mostrarDetallesDelLibro(libroDTO);
                                        libroRegistrado = true;
                                        break;
                                    }
                                }
                            }
                            if (!libroRegistrado) {
                                System.out.println("No se encontró un libro con el título '" + tituloLibro + "' en la API");
                            }
                        }
                    } catch (Exception e) {
                        System.out.println("Error al obtener datos de la API: " + e.getMessage());
                    }
                    break;
                case 2:
                    libroService.listarLibros().forEach(libro -> {
                        System.out.println("------LIBRO--------");
                        System.out.println("Título: " + libro.getTitulo());
                        System.out.println("Autor: " +
                                (libro.getAutor() != null ? libro.getAutor().getNombre() : "Desconocido"));
                        System.out.println("Idioma: " + libro.getIdioma());
                        System.out.println("Número de descargas: " + libro.getNumeroDescargas());
                    });
                    break;
                case 3:
                    autorService.listarAutores().forEach(autor -> {
                        System.out.println("-------AUTOR-------");
                        System.out.println("Autor: " + autor.getNombre());
                        System.out.println("Fecha de nacimiento: " + autor.getAñoNacimiento());
                        System.out.println("Fecha de fallecimiento: " +
                                (autor.getAñoFallecimiento() != null ? autor.getAñoFallecimiento() : "Desconocido"));
                        String libros = autor.getLibros().stream()
                                .map(Libro::getTitulo)
                                .collect(Collectors.joining(", "));
                        System.out.println("Libros: [ " + libros + " ]");
                    });
                    break;
                case 4:
                    System.out.print("Ingrese el año vivo de autor(es) que desea buscar: ");
                    int año = scanner.nextInt();
                    scanner.nextLine();
                    List<Autor> autoresVivos = autorService.listarAutoresVivosEnAñoEspecifico(año);
                    if (autoresVivos.isEmpty()) {
                        System.out.println("No se encontraron autores vivos en el año " + año);
                    } else {
                        autoresVivos.forEach(autor -> {
                            System.out.println("-------AUTOR-------");
                            System.out.println("Autor: " + autor.getNombre());
                            System.out.println("Fecha de nacimiento: " + autor.getAñoNacimiento());
                            System.out.println("Fecha de fallecimiento: " +
                                    (autor.getAñoFallecimiento() != null ? autor.getAñoFallecimiento() : "Desconocido"));
                            System.out.println("Libros: " + autor.getLibros().size());
                        });
                    }
                    break;
                case 5:
                    System.out.println("Ingrese el idioma:");
                    System.out.println("es");
                    System.out.println("en");
                    System.out.println("fr");
                    System.out.println("pt");
                    String idioma = scanner.nextLine();
                    if ("es".equalsIgnoreCase(idioma) || "en".equalsIgnoreCase(idioma) || "fr".equalsIgnoreCase(idioma)
                            || "pt".equalsIgnoreCase(idioma)) {
                        libroService.listarLibrosPorIdioma(idioma).forEach(libro -> {
                            System.out.println("------LIBRO--------");
                            System.out.println("Título: " + libro.getTitulo());
                            System.out.println("Autor: " +
                                    (libro.getAutor() != null ? libro.getAutor().getNombre() : "Desconocido"));
                            System.out.println("Idioma: " + libro.getIdioma());
                            System.out.println("Número de descargas: " + libro.getNumeroDescargas());
                        });
                    } else {
                        System.out.println("Idioma no válido.");
                    }
                    break;
                case 0:
                    System.out.println("Finalizando operaciones...");
                    break;
                default:
                    System.out.println("Opción no válida.");
            }
        } while (opcionUsuario != 0);

        scanner.close();
    }

    private void mostrarDetallesDelLibro(LibroDTO libroDTO) {
        System.out.println("------LIBRO--------");
        System.out.println("Título: " + libroDTO.getTitulo());
        System.out.println("Autor: " +
                (libroDTO.getAutores().isEmpty() ? "Desconocido" : libroDTO.getAutores().get(0).getNombre()));
        System.out.println("Idioma: " + libroDTO.getIdiomas().get(0));
        System.out.println("Número de descargas: " + libroDTO.getNumeroDeDescargas());
    }
}
