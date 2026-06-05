package com.devsu.backend.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "cliente")
@PrimaryKeyJoinColumn(name = "id")

public class Cliente extends Persona{

    @Column(unique = true,nullable = false)
    private String clienteId;

    private String contrasena;

    private Boolean estado;
}
