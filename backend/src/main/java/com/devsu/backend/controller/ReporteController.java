package com.devsu.backend.controller;

import com.devsu.backend.dto.ReporteDTO;
import com.devsu.backend.service.ReporteService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/reportes")
@RequiredArgsConstructor
public class ReporteController {

    private final ReporteService reporteService;

    @GetMapping
    public ResponseEntity<Map<String, Object>> getReporte(
            @RequestParam Long clienteId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate fechaFin
    ) throws Exception {

        List<ReporteDTO> reporte =  reporteService.getReporte(clienteId, fechaInicio, fechaFin);
        String pdfBase64 = reporteService.generarPDFBase64(clienteId, fechaInicio, fechaFin);
        Map<String, Object> response = new HashMap<>();
        response.put("reporte", reporte);
        response.put("pdf", pdfBase64);

        return ResponseEntity.ok(response);

    }
}
