package com.devsu.backend.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "cliente")
@PrimaryKeyJoinColumn(name = "id")

public class Cliente extends Persona{

    @Column(unique = true,nullable = false)
    @NotBlank(message = "El clienteId es obligatorio")
    private String clienteId;

    @NotBlank(message = "La contrasena es obligatoria")
    private String contrasena;

    @NotNull(message = "El estado es oblogatorio")
    private Boolean estado;
}
