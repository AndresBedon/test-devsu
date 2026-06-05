package com.devsu.backend.controller;


import com.devsu.backend.model.Movimiento;
import com.devsu.backend.service.MovimientoService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/movimientos")
@RequiredArgsConstructor
public class MovimientoController {

    private final MovimientoService movimientoService;

    @GetMapping
    public ResponseEntity<List<Movimiento>> findAll(){
        return ResponseEntity.ok(movimientoService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Movimiento> findById(@PathVariable Long id){
        return ResponseEntity.ok(movimientoService.findById(id));
    }

    @PostMapping
    public ResponseEntity<Movimiento> save(@RequestBody Movimiento movimiento){
        return ResponseEntity.status(HttpStatus.CREATED).body(movimientoService.save(movimiento));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id){
        movimientoService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/reportes")
    public ResponseEntity<List<Movimiento>> getReporte(
            @RequestParam Long clienteId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate fechaFin
    ){
        return ResponseEntity.ok(movimientoService.getReporte(clienteId, fechaInicio, fechaFin));
    }
}
