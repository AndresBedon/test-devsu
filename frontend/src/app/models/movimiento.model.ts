export interface Movimiento {
	id?: number;
    fecha?: string;
    tipoMovimiento: string;
	valor: number;
    saldo?: number;
    cuenta?: {
        id: number;
        numeroCuenta?: string;
        tipoCuenta?: string;
        clienteNombre?: string;
        cliente?: {
            id: number;
            nombre?: string;
        };
    };
}
