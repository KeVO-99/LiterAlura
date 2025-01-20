package com.alura.literalura.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record LibroDTO(
        @JsonProperty("id")
        int id,
        @JsonProperty("title")
        String titulo,
        @JsonProperty("authors")
        List<AutorDTO> autores,
        @JsonProperty("languages")
        List<String> idiomas,
        @JsonProperty("download_count")
        int numeroDescargas) {

    public String getTitulo() {
        return titulo;
    }

    public List<String> getIdiomas() {
        return idiomas;
    }

    public int getNumeroDeDescargas() {
        return numeroDescargas;
    }

    public List<AutorDTO> getAutores() {
        return autores;
    }
}
