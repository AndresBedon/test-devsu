package com.devsu.backend.controller;

import com.devsu.backend.dto.CuentaDTO;
import com.devsu.backend.model.Cuenta;
import com.devsu.backend.service.CuentaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/cuentas")
@RequiredArgsConstructor
public class CuentaController {

    private final CuentaService cuentaService;

    @GetMapping
    public ResponseEntity<List<CuentaDTO>> findAll(){
        return ResponseEntity.ok(cuentaService.findAll().stream().map(cuentaService::toDTO).collect(Collectors.toList()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CuentaDTO> findById(@PathVariable Long id){
        return ResponseEntity.ok(cuentaService.toDTO(cuentaService.findById(id)));
    }

    @PostMapping
    public ResponseEntity<CuentaDTO> save(@RequestBody Cuenta cuenta){
        return ResponseEntity.status(HttpStatus.CREATED).body(cuentaService.toDTO(cuentaService.save(cuenta)) );
    }

    @PutMapping("/{id}")
    public ResponseEntity<CuentaDTO> update(@PathVariable Long id, @RequestBody Cuenta cuenta){
        return ResponseEntity.ok(cuentaService.toDTO(cuentaService.update(id, cuenta)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id){
        cuentaService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
