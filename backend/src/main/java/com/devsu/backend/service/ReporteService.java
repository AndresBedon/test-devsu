package com.devsu.backend.service;



import com.devsu.backend.dto.ReporteDTO;
import com.devsu.backend.model.Movimiento;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReporteService {

    private final MovimientoService movimientoService;

    public List<ReporteDTO> getReporte(
            Long clienteId,
            LocalDate fechaInicio,
            LocalDate fechaFin
    ){
        List<Movimiento> movimientos = movimientoService.getReporte(clienteId, fechaInicio, fechaFin);

        return movimientos.stream().map(m -> {
            ReporteDTO dto = new ReporteDTO();
            dto.setFecha(m.getFecha());
            dto.setCliente(m.getCuenta().getCliente().getNombre());
            dto.setNumeroCuenta(m.getCuenta().getNumeroCuenta());
            dto.setTipo(m.getCuenta().getTipoCuenta());
            dto.setSaldoInicial(m.getCuenta().getSaldoInicial());
            dto.setEstado(m.getCuenta().getEstado());
            dto.setMovimiento(m.getValor());
            dto.setSaldoDisponible(m.getSaldo());
            return dto;
        }).collect(Collectors.toList());
    }

    public String generarPDFBase64(
            Long clienteId,
            LocalDate fechaInicio,
            LocalDate fechaFin) throws Exception {

        List<ReporteDTO> reporte = getReporte(
                clienteId, fechaInicio, fechaFin);

        Document document = new Document();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter.getInstance(document, baos);
        document.open();

        // Título
        Font titleFont = new Font(Font.FontFamily.HELVETICA, 16, Font.BOLD);
        Paragraph title = new Paragraph("Estado de Cuenta", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        document.add(title);
        document.add(Chunk.NEWLINE);

        // Tabla
        PdfPTable table = new PdfPTable(8);
        table.setWidthPercentage(100);

        // Headers
        String[] headers = {"Fecha", "Cliente", "Num. Cuenta",
                "Tipo", "Saldo Inicial", "Estado", "Movimiento", "Saldo"};
        for (String header : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(header,
                    new Font(Font.FontFamily.HELVETICA, 8, Font.BOLD)));
            cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);
        }

        // Datos
        for (ReporteDTO r : reporte) {
            table.addCell(r.getFecha().toString());
            table.addCell(r.getCliente());
            table.addCell(r.getNumeroCuenta());
            table.addCell(r.getTipo());
            table.addCell(r.getSaldoInicial().toString());
            table.addCell(r.getEstado() ? "Activa" : "Inactiva");
            table.addCell(r.getMovimiento().toString());
            table.addCell(r.getSaldoDisponible().toString());
        }

        document.add(table);
        document.close();

        return Base64.getEncoder().encodeToString(baos.toByteArray());
    }
}
