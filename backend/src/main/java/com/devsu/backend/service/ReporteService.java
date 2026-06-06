package com.devsu.backend.service;



import com.devsu.backend.dto.ReporteDTO;
import com.devsu.backend.model.Cuenta;
import com.devsu.backend.model.Movimiento;
import com.devsu.backend.repository.CuentaRepository;
import com.devsu.backend.repository.MovimientoRepository;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReporteService {

    private final MovimientoService movimientoService;
    private final MovimientoRepository movimientoRepository;
    private final CuentaRepository cuentaRepository;

    public List<ReporteDTO> getReporte(
            Long clienteId,
            LocalDate fechaInicio,
            LocalDate fechaFin
    ){
        //Obtener todas las cuentas del cliente
        List<Cuenta> cuentas = cuentaRepository.findByClienteId(clienteId);

        List<ReporteDTO> resultado = new ArrayList<>();
        for (Cuenta cuenta : cuentas){
            //Obtener movimientos de esta cuenta en el rango de fechas
            List<Movimiento> movimientos = movimientoRepository.findByCuentaIdAndFechaBetween(cuenta.getId(), fechaInicio, fechaFin);

            if(movimientos.isEmpty()){
                //Si no tiene movimientos igual aprece con el saldo incial
                ReporteDTO dto = new ReporteDTO();
                dto.setFecha(fechaInicio);
                dto.setCliente(cuenta.getCliente().getNombre());
                dto.setNumeroCuenta(cuenta.getNumeroCuenta());
                dto.setTipo(cuenta.getTipoCuenta());
                dto.setSaldoInicial(cuenta.getSaldoInicial());
                dto.setEstado(cuenta.getEstado());
                dto.setMovimiento(0.0);
                dto.setSaldoDisponible(cuenta.getSaldoDisponible());
                resultado.add(dto);
            }else {
                //Si tiene movimientos los agrega todos
                movimientos.forEach(m -> {
                    ReporteDTO dto = new ReporteDTO();
                    dto.setFecha(m.getFecha());
                    dto.setCliente(cuenta.getCliente().getNombre());
                    dto.setNumeroCuenta(cuenta.getNumeroCuenta());
                    dto.setTipo(cuenta.getTipoCuenta());
                    dto.setSaldoInicial(cuenta.getSaldoInicial());
                    dto.setEstado(cuenta.getEstado());
                    dto.setMovimiento(m.getValor());
                    dto.setSaldoDisponible(m.getSaldo());
                    resultado.add(dto);
                });
            }
        }
        return  resultado;
    }

    public String generarPDFBase64(
            Long clienteId,
            LocalDate fechaInicio,
            LocalDate fechaFin) throws Exception {

        List<ReporteDTO> reporte = getReporte(clienteId, fechaInicio, fechaFin);

        Document document = new Document();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter.getInstance(document, baos);
        document.open();

        // Título
        Font titleFont = new Font(Font.FontFamily.HELVETICA, 16, Font.BOLD);
        Font headerFont = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD);
        Font normalFont = new Font(Font.FontFamily.HELVETICA, 9, Font.NORMAL);
        Font boldFont = new Font(Font.FontFamily.HELVETICA, 9, Font.BOLD);

        Paragraph title = new Paragraph("Estado de Cuenta", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        document.add(title);

        Paragraph subtitle = new Paragraph(
                "Período: " + fechaInicio + " al " + fechaFin, normalFont);
        subtitle.setAlignment(Element.ALIGN_CENTER);
        document.add(subtitle);
        document.add(Chunk.NEWLINE);

        // Agrupar por cuenta
        Map<String, List<ReporteDTO>> agrupado = new LinkedHashMap<>();
        for (ReporteDTO r : reporte) {
            agrupado.computeIfAbsent(r.getNumeroCuenta(), k -> new ArrayList<>()).add(r);
        }

        // Por cada cuenta
        for (Map.Entry<String, List<ReporteDTO>> entry : agrupado.entrySet()) {
            List<ReporteDTO> movimientos = entry.getValue();
            ReporteDTO primera = movimientos.get(0);

            // Header de la cuenta
            PdfPTable headerTable = new PdfPTable(4);
            headerTable.setWidthPercentage(100);
            headerTable.setSpacingBefore(10f);

            PdfPCell h1 = new PdfPCell(new Phrase("Cuenta: " + primera.getNumeroCuenta(), headerFont));
            PdfPCell h2 = new PdfPCell(new Phrase("Tipo: " + primera.getTipo(), headerFont));
            PdfPCell h3 = new PdfPCell(new Phrase("Saldo Inicial: $" + primera.getSaldoInicial(), headerFont));
            PdfPCell h4 = new PdfPCell(new Phrase("Estado: " + (primera.getEstado() ? "Activo" : "Inactivo"), headerFont));

            for (PdfPCell cell : new PdfPCell[]{h1, h2, h3, h4}) {
                cell.setBackgroundColor(new BaseColor(220, 220, 220));
                cell.setPadding(6);
                headerTable.addCell(cell);
            }
            document.add(headerTable);

            // Tabla de movimientos
            PdfPTable table = new PdfPTable(4);
            table.setWidthPercentage(100);

            // Headers
            String[] headers = {"Fecha", "Cliente", "Movimiento", "Saldo Disponible"};
            for (String h : headers) {
                PdfPCell cell = new PdfPCell(new Phrase(h, boldFont));
                cell.setBackgroundColor(new BaseColor(26, 26, 46));
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setPadding(6);
                // Color blanco para el texto
                cell.setPhrase(new Phrase(h, new Font(Font.FontFamily.HELVETICA, 9, Font.BOLD, BaseColor.WHITE)));
                table.addCell(cell);
            }

            // Filas
            double totalDebitos = 0;
            double totalCreditos = 0;
            double saldoFinal = 0;

            for (ReporteDTO r : movimientos) {
                table.addCell(new Phrase(r.getFecha().toString(), normalFont));
                table.addCell(new Phrase(r.getCliente(), normalFont));

                PdfPCell movCell = new PdfPCell(new Phrase("$" + r.getMovimiento(), normalFont));
                if (r.getMovimiento() < 0) {
                    movCell.setPhrase(new Phrase("$" + r.getMovimiento(),
                            new Font(Font.FontFamily.HELVETICA, 9, Font.NORMAL, BaseColor.RED)));
                    totalDebitos += Math.abs(r.getMovimiento());
                } else {
                    movCell.setPhrase(new Phrase("$" + r.getMovimiento(),
                            new Font(Font.FontFamily.HELVETICA, 9, Font.NORMAL, new BaseColor(46, 125, 50))));
                    totalCreditos += r.getMovimiento();
                }
                table.addCell(movCell);
                table.addCell(new Phrase("$" + r.getSaldoDisponible(), normalFont));
                saldoFinal = r.getSaldoDisponible();
            }

            // Fila de totales
            PdfPCell totalLabel = new PdfPCell(new Phrase("Totales", boldFont));
            totalLabel.setColspan(1);
            totalLabel.setBackgroundColor(new BaseColor(240, 240, 240));
            totalLabel.setPadding(6);
            table.addCell(totalLabel);

            PdfPCell emptyCell = new PdfPCell(new Phrase(""));
            emptyCell.setBackgroundColor(new BaseColor(240, 240, 240));
            table.addCell(emptyCell);

            PdfPCell totalesCell = new PdfPCell();
            totalesCell.setBackgroundColor(new BaseColor(240, 240, 240));
            totalesCell.setPadding(6);
            totalesCell.addElement(new Phrase("Retiros: $" + totalDebitos,
                    new Font(Font.FontFamily.HELVETICA, 9, Font.BOLD, BaseColor.RED)));
            totalesCell.addElement(new Phrase("Depositos: $" + totalCreditos,
                    new Font(Font.FontFamily.HELVETICA, 9, Font.BOLD, new BaseColor(46, 125, 50))));
            table.addCell(totalesCell);

            PdfPCell saldoFinalCell = new PdfPCell(
                    new Phrase("Saldo Disponible: $" + saldoFinal, boldFont));
            saldoFinalCell.setBackgroundColor(new BaseColor(240, 240, 240));
            saldoFinalCell.setPadding(6);
            table.addCell(saldoFinalCell);

            document.add(table);
            document.add(Chunk.NEWLINE);
        }

        document.close();
        return Base64.getEncoder().encodeToString(baos.toByteArray());
    }
}
