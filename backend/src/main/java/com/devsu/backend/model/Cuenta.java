package com.devsu.backend.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Entity
@Table(name = "cuenta")

public class Cuenta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(unique = true, nullable = false)
    @NotBlank(message = "El numero de cuenta es obligatorio")
    private String numeroCuenta;

    @Column(nullable = false)
    @NotBlank(message = "El tipo de cuenta es obligatorio")
    private String tipoCuenta;

    @Column(nullable = false)
    @NotNull(message = "El saldo inicial es obligatorio")
    @Min(value = 0, message ="El saldo inicial no puede ser negativo")
    private Double saldoInicial;

    @Column(nullable = false)
    private Double saldoDisponible;

    private Boolean estado;

    @ManyToOne
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;
}
