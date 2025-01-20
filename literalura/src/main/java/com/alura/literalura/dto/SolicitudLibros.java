package com.alura.literalura.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record SolicitudLibros(@JsonProperty("results") List<LibroDTO> libros) {
    public List<LibroDTO> getLibros() {
        return libros;
    }
}
