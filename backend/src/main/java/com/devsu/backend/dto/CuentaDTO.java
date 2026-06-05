package com.devsu.backend.dto;

import lombok.Data;

@Data
public class CuentaDTO {
    private Long id;
    private String numeroCuenta;
    private String tipoCuenta;
    private Double saldoInicial;
    private Double saldoDisponible;
    private Boolean estado;
    private String clienteNombre;
    private Long clienteId;
}
