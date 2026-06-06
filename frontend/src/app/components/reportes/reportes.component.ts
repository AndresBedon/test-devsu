import { Component, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ReporteService } from '../../services/reporte.service';
import { ClienteService } from '../../services/cliente.service';
import { Reporte, ReporteAgrupado } from '../../models/reporte.model';
import { Cliente } from '../../models/cliente.model';

@Component({
    selector: 'app-reportes',
    standalone: true,
    imports: [CommonModule, FormsModule],
    templateUrl: './reportes.component.html',
    styleUrls: ['./reportes.component.css']
})
export class ReportesComponent {

    clientes: Cliente[] = [];
    reportes: Reporte[] = [];
    reportesAgrupados: ReporteAgrupado[] = [];
    clienteId: number = 0;
    fechaInicio: string = '';
    fechaFin: string = '';
    mensaje: string = '';
    tipoMensaje: string = '';
    cargando: boolean = false;

    constructor(
        private reporteService: ReporteService,
        private clienteService: ClienteService,
        private cdr: ChangeDetectorRef
    ) {
        this.clienteService.getAll().subscribe({
            next: (data) => {
                this.clientes = data
                this.cdr.detectChanges();
            }
        });
    }

    buscarReporte(): void {
        if (!this.clienteId || !this.fechaInicio || !this.fechaFin) {
            this.mostrarMensaje('Complete todos los campos', 'error');
            return;
        }

        this.cargando = true;
        this.reporteService.getReporte(
            this.clienteId,
            this.fechaInicio,
            this.fechaFin
        ).subscribe({
            next: (data) => {
                this.reportes = [...data.reporte];
                this.reportesAgrupados = this.agruparPorCuenta(this.reportes);
                this.cargando = false;
                this.cdr.detectChanges();
                if (this.reportes.length === 0) {
                    this.mostrarMensaje('No hay movimientos en ese rango de fechas', 'error');
                }
            },
            error: () => {
                this.cargando = false;
                this.mostrarMensaje('Error al obtener reporte', 'error');
            }
        });
    }

    agruparPorCuenta(reportes: Reporte[]): ReporteAgrupado[] {
        const mapa = new Map<string, ReporteAgrupado>();
        reportes.forEach(r => {
            if (!mapa.has(r.numeroCuenta)) {
                mapa.set(r.numeroCuenta, {
                    numeroCuenta: r.numeroCuenta,
                    tipo: r.tipo,
                    saldoInicial: r.saldoInicial,
                    estado: r.estado,
                    movimientos: [],
                    totalDebitos: 0,
                    totalCreditos: 0,
                    saldoFinal: 0
                });
            }
            const grupo = mapa.get(r.numeroCuenta)!;
            grupo.movimientos.push(r);
            if (r.movimiento < 0) {
                grupo.totalDebitos += Math.abs(r.movimiento);
            } else {
                grupo.totalCreditos += r.movimiento;
            }
            grupo.saldoFinal = r.saldoDisponible;
        });

        //El saldo final es el saldo disponible del último movimiento
        return Array.from(mapa.values());
    }

    descargarPDF(): void {
        if (!this.clienteId || !this.fechaInicio || !this.fechaFin) {
            this.mostrarMensaje('Complete todos los campos', 'error');
            return;
        }

        this.reporteService.getReporte(
            this.clienteId,
            this.fechaInicio,
            this.fechaFin
        ).subscribe({
            next: (data) => {
                const base64 = data.pdf;
                const byteChars = atob(base64);
                const byteNums = new Array(byteChars.length);
                for (let i = 0; i < byteChars.length; i++) {
                    byteNums[i] = byteChars.charCodeAt(i);
                }
                const byteArray = new Uint8Array(byteNums);
                const blob = new Blob([byteArray], { type: 'application/pdf' });
                const url = URL.createObjectURL(blob);
                const link = document.createElement('a');
                link.href = url;
                link.download = `reporte-${this.fechaInicio}-${this.fechaFin}.pdf`;
                link.click();
                URL.revokeObjectURL(url);
            },
            error: () => this.mostrarMensaje('Error al descargar PDF', 'error')
        });
    }

    mostrarMensaje(texto: string, tipo: string): void {
        this.mensaje = texto;
        this.tipoMensaje = tipo;
        setTimeout(() => {
            this.mensaje = '';
            this.cdr.detectChanges();
        }, 4000);
    }
}