package com.devsu.backend.model;
import jakarta.persistence.*;
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
    private String nombre;

    private String genero;

    private Integer edad;

    @Column(unique = true,nullable = false)
    private String identificacion;

    private String direccion;

    private String telefono;

}
