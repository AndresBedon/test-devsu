export interface Cuenta {
	id?: number;
	numeroCuenta: string;
	tipoCuenta: string;
	saldoInicial: number;
    saldoDisponible?: number;
    estado: boolean;
    cliente?: {
        id: number;
    };
	clienteNombre?: string;
	clienteId?: number;
}
