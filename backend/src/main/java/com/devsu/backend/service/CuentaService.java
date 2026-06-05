package com.devsu.backend.service;

import com.devsu.backend.dto.CuentaDTO;
import com.devsu.backend.exception.ResourceNotFoundException;
import com.devsu.backend.model.Cliente;
import com.devsu.backend.model.Cuenta;
import com.devsu.backend.repository.CuentaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CuentaService {

    private final CuentaRepository cuentaRepository;
    private final ClienteService clienteService;

    public List<Cuenta> findAll() {
        return this.cuentaRepository.findAll();
    }

    public Cuenta findById(Long id) {
        return this.cuentaRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Cuenta no encontrada con id " + id));
    }

    public Cuenta save(Cuenta cuenta) {
        //Buscar Cliente
        Cliente cliente = clienteService.findById(cuenta.getCliente().getId());
        cuenta.setCliente(cliente);
        cuenta.setSaldoDisponible(cuenta.getSaldoInicial());
        return this.cuentaRepository.save(cuenta);
    }

    public Cuenta updateSaldoDisponible(Cuenta cuenta) {
        return this.cuentaRepository.save(cuenta);
    }

    public Cuenta update(Long id,Cuenta cuenta) {
        Cuenta existing = this.findById(id);

        // Al actualizar saldo inicial, resetea saldo disponible
        if(cuenta.getSaldoInicial() != null ) {
            existing.setSaldoDisponible(existing.getSaldoInicial());
            existing.setSaldoInicial(cuenta.getSaldoInicial());
        }
        existing.setNumeroCuenta( cuenta.getNumeroCuenta());
        existing.setTipoCuenta( cuenta.getTipoCuenta());
        existing.setEstado(cuenta.getEstado());
        return this.cuentaRepository.save(existing);
    }

    public void delete(Long id) {
        findById(id);
        cuentaRepository.deleteById(id);
    }

    public CuentaDTO toDTO(Cuenta cuenta) {
        CuentaDTO dto = new CuentaDTO();
        dto.setId( cuenta.getId() );
        dto.setNumeroCuenta(cuenta.getNumeroCuenta());
        dto.setTipoCuenta(cuenta.getTipoCuenta());
        dto.setSaldoInicial(cuenta.getSaldoInicial());
        dto.setSaldoDisponible(cuenta.getSaldoDisponible());
        dto.setEstado(cuenta.getEstado());
        dto.setClienteNombre(cuenta.getCliente().getNombre());
        dto.setClienteId(cuenta.getCliente().getId());
        return dto;
    }
}
