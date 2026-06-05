package com.devsu.backend.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class ReporteDTO {
    private LocalDate fecha;
    private String cliente;
    private String numeroCuenta;
    private String tipo;
    private Double SaldoInicial;
    private Boolean estado;
    private Double movimiento;
    private Double saldoDisponible;
}
