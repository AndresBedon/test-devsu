export interface Reporte {
    fecha: string;
    cliente: string;
    numeroCuenta: string;
    tipo: string;
    saldoInicial: number;
    estado: boolean;
    movimiento: number;
    saldoDisponible: number;
}

export interface ReporteAgrupado {
    numeroCuenta: string;
    tipo: string;
    saldoInicial: number;
    estado: boolean;
    movimientos: Reporte[];
    totalDebitos: number;
    totalCreditos: number;
    saldoFinal: number;
}
