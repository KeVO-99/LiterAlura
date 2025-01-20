package com.alura.literalura.service;

import com.alura.literalura.model.Autor;
import com.alura.literalura.repository.AutorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AutorService {
    @Autowired
    private AutorRepository autorRepository;

    public List<Autor> listarAutores() {
        return autorRepository.findAllConLibros();
    }

    public List<Autor> listarAutoresVivosEnAñoEspecifico(int año) {
        return autorRepository.findAutoresVivosEnAñoConLibros(año);
    }

    public Autor crearAutor(Autor autor) {
        return autorRepository.save(autor);
    }

    public Optional<Autor> obtenerAutorPorId(Long id) {
        return autorRepository.findById(id);
    }

    public void eliminarAutor(Long id) {
        autorRepository.deleteById(id);
    }

    public Optional<Autor> obtenerAutorPorNombre(String nombre) {
        return autorRepository.findByNombre(nombre);
    }

    public Autor actualizarAutor(Long id, Autor autorDetalles) {
        Autor autor = autorRepository.findById(id).orElseThrow(() -> new RuntimeException("Autor no encontrado"));
        autor.setNombre(autorDetalles.getNombre());
        autor.setAñoNacimiento(autorDetalles.getAñoNacimiento());
        autor.setAñoFallecimiento(autorDetalles.getAñoFallecimiento());
        return autorRepository.save(autor);
    }
}
