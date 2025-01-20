package com.alura.literalura.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record AutorDTO(
        @JsonProperty("name")
        String nombre,
        @JsonProperty("birth_year")
        int añoNacimiento,
        @JsonProperty("death_year")
        int añoFallecimiento) {

    public String getNombre() {
        return nombre;
    }

    public int getAñoNacimiento() {
        return añoNacimiento;
    }

    public int getAñoFallecimiento() {
        return añoFallecimiento;
    }
}
