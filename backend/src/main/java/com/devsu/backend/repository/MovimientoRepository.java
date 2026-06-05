package com.devsu.backend.repository;

import com.devsu.backend.model.Movimiento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface MovimientoRepository extends JpaRepository<Movimiento, Long> {

    List<Movimiento> findByCuentaId(Long cuentaId);

    List<Movimiento> findByCuentaClienteIdAndFechaBetween(
            Long clienteId,
            LocalDate fechaInicio,
            LocalDate fechaFin
    );
}
