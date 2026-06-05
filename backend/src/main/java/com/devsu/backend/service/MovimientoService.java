package com.devsu.backend.service;


import com.devsu.backend.exception.ResourceNotFoundException;
import com.devsu.backend.model.Cuenta;
import com.devsu.backend.model.Movimiento;
import com.devsu.backend.repository.MovimientoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MovimientoService {

    private final MovimientoRepository movimientoRepository;
    private final CuentaService cuentaService;

    private static final Double LIMITE_DIARIO = 1000.0;

    public List<Movimiento> findAll() {
        return this.movimientoRepository.findAll();
    }

    public Movimiento findById(Long id) {
        return this.movimientoRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Movimiento no encontrado con id" + id));
    }

    public Movimiento save(Movimiento movimiento) {
        Cuenta cuenta = this.cuentaService.findById(movimiento.getCuenta().getId());

        Double valor = movimiento.getValor();
        Double saldoActual = cuenta.getSaldoDisponible();

        // Validar si es debito
        if(valor < 0 ){

            // Validar saldo disponible
            if(saldoActual <= 0 ){
                throw  new RuntimeException("Saldo no disponible");
            }

            //Validar que el retiro no supere el saldo actual
            if(Math.abs(valor) > saldoActual ){
                throw  new RuntimeException("Saldo no disponible");
            }

            // Validar limite diario
            Double totalRetiradoHoy = getTotalRetiradoHoy(cuenta.getId());
            if(totalRetiradoHoy >=  LIMITE_DIARIO){
                throw  new RuntimeException("Cupo diario Excedido");
            }

            // Validar que el retiro no supere el limite diario restante
            Double limiteRestante = LIMITE_DIARIO - totalRetiradoHoy;
            if(Math.abs(valor) > limiteRestante){
                throw  new RuntimeException("Cupo diario Excedido");
            }
        }

        // Calcular nuevo saldo
        Double nuevoSaldo = saldoActual + valor;
        cuenta.setSaldoDisponible(nuevoSaldo);
        cuentaService.updateSaldoDisponible(cuenta);

        //Guardar movimiento
        movimiento.setFecha(LocalDate.now());
        movimiento.setSaldo(nuevoSaldo);
        movimiento.setCuenta(cuenta);

        return this.movimientoRepository.save(movimiento);
    }

    public void delete(Long id) {
        findById(id);
        movimientoRepository.deleteById(id);
    }

    private Double getTotalRetiradoHoy(Long cuentaId) {
        LocalDate hoy = LocalDate.now();
        return movimientoRepository
                .findByCuentaId(cuentaId)
                .stream()
                .filter(m -> m.getFecha().isEqual(hoy))
                .filter(m -> m.getValor() < 0 )
                .mapToDouble(m -> Math.abs(m.getValor()))
                .sum();
    }

    public List<Movimiento> getReporte(
            Long clienteId,
            LocalDate fechaInicio,
            LocalDate fechaFin
    ){
        return movimientoRepository.findByCuentaClienteIdAndFechaBetween(clienteId, fechaInicio, fechaFin);
    }
}
