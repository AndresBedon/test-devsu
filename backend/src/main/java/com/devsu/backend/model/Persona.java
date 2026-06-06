package com.devsu.backend.model;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Entity
@Table(name = "persona")
@Inheritance(strategy = InheritanceType.JOINED)
public class Persona {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false)
    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    @NotBlank(message = "El genero es obligatorio")
    private String genero;

    @NotNull(message = "La edad es obliogatoria")
    @Min(value = 1, message = "La edad debe ser mayor a 0")
    private Integer edad;

    @Column(unique = true,nullable = false)
    @NotBlank(message = "La identificacion es obligatorio")
    private String identificacion;

    @NotBlank(message = "La direccion es obligatorio")
    private String direccion;

    @NotBlank(message = "El telefono es obligatorio")
    private String telefono;

}
