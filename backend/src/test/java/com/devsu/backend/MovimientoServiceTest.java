package com.devsu.backend;

import com.devsu.backend.model.Cliente;
import com.devsu.backend.model.Cuenta;
import com.devsu.backend.model.Movimiento;
import com.devsu.backend.repository.MovimientoRepository;
import com.devsu.backend.service.CuentaService;
import com.devsu.backend.service.MovimientoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
        import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MovimientoServiceTest {

    @Mock
    private MovimientoRepository movimientoRepository;

    @Mock
    private CuentaService cuentaService;

    @InjectMocks
    private MovimientoService movimientoService;

    private Cuenta cuenta;
    private Cliente cliente;

    @BeforeEach
    void setUp() {
        cliente = new Cliente();
        cliente.setId(1L);
        cliente.setNombre("Jose Lema");
        cliente.setEstado(true);

        cuenta = new Cuenta();
        cuenta.setId(1L);
        cuenta.setNumeroCuenta("478758");
        cuenta.setTipoCuenta("Ahorro");
        cuenta.setSaldoInicial(2000.0);
        cuenta.setSaldoDisponible(2000.0);
        cuenta.setEstado(true);
        cuenta.setCliente(cliente);
    }

    // Test 1 - Movimiento débito exitoso
    @Test
    void cuandoRetiroEsValido_debeActualizarSaldo() {
        Movimiento movimiento = new Movimiento();
        movimiento.setValor(-575.0);
        movimiento.setTipoMovimiento("Retiro");
        movimiento.setCuenta(cuenta);

        when(cuentaService.findById(1L)).thenReturn(cuenta);
        when(movimientoRepository.findByCuentaId(1L))
                .thenReturn(new ArrayList<>());
        when(movimientoRepository.save(any(Movimiento.class)))
                .thenAnswer(i -> i.getArguments()[0]);

        Movimiento resultado = movimientoService.save(movimiento);

        assertEquals(1425.0, resultado.getSaldo());
        assertEquals(LocalDate.now(), resultado.getFecha());
        verify(movimientoRepository, times(1)).save(any());
    }

    // Test 2 - Saldo no disponible
    @Test
    void cuandoSaldoEsCero_debeLanzarExcepcion() {
        cuenta.setSaldoDisponible(0.0);

        Movimiento movimiento = new Movimiento();
        movimiento.setValor(-100.0);
        movimiento.setTipoMovimiento("Retiro");
        movimiento.setCuenta(cuenta);

        when(cuentaService.findById(1L)).thenReturn(cuenta);

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> movimientoService.save(movimiento)
        );

        assertEquals("Saldo no disponible", exception.getMessage());
    }

    // Test 3 - Cupo diario excedido
    @Test
    void cuandoCupoDiarioExcedido_debeLanzarExcepcion() {
        Movimiento movimientoPrevio = new Movimiento();
        movimientoPrevio.setValor(-1000.0);
        movimientoPrevio.setFecha(LocalDate.now());
        movimientoPrevio.setCuenta(cuenta);

        Movimiento movimiento = new Movimiento();
        movimiento.setValor(-100.0);
        movimiento.setTipoMovimiento("Retiro");
        movimiento.setCuenta(cuenta);

        when(cuentaService.findById(1L)).thenReturn(cuenta);
        when(movimientoRepository.findByCuentaId(1L))
                .thenReturn(java.util.List.of(movimientoPrevio));

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> movimientoService.save(movimiento)
        );

        assertEquals("Cupo diario Excedido", exception.getMessage());
    }

    // Test 4 - Depósito exitoso
    @Test
    void cuandoDepositoEsValido_debeAumentarSaldo() {
        Movimiento movimiento = new Movimiento();
        movimiento.setValor(500.0);
        movimiento.setTipoMovimiento("Deposito");
        movimiento.setCuenta(cuenta);

        when(cuentaService.findById(1L)).thenReturn(cuenta);
        when(movimientoRepository.save(any(Movimiento.class)))
                .thenAnswer(i -> i.getArguments()[0]);

        Movimiento resultado = movimientoService.save(movimiento);

        assertEquals(2500.0, resultado.getSaldo());
        verify(movimientoRepository, times(1)).save(any());
    }
}