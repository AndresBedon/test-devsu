package com.devsu.backend.repository;

import com.devsu.backend.model.Cuenta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CuentaRepository extends JpaRepository<Cuenta,Long> {

    @Query("SELECT c FROM Cuenta c WHERE c.cliente.id = :clienteId")
    List<Cuenta> findByClienteId(@Param("clienteId") Long clienteId);

    @Query("SELECT c FROM Cuenta c WHERE c.numeroCuenta = :numeroCuenta")
    Cuenta findByNumeroCuenta(@Param("numeroCuenta") String numeroCuenta);
}
