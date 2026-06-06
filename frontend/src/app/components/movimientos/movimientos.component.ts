import { CommonModule } from '@angular/common';
import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Cuenta } from '../../models/cuenta.model';
import { CuentaService } from '../../services/cuenta.service';
import { Movimiento } from '../../models/movimiento.model';
import { MovimientoService } from '../../services/movimiento.service';



@Component({
	selector: 'app-movimientos',
    standalone: true,
    imports: [CommonModule, FormsModule],
	templateUrl: './movimientos.component.html',
    styleUrl: './movimientos.component.css'
})
export class MovimientosComponent implements OnInit {
	movimientos: Movimiento[] = [];
    movimientosFiltrados: Movimiento[] = [];
    cuentas: Cuenta[] = [];
    movimientoForm: Movimiento = this.initForm();
    mostrarModal: boolean = false;
    busqueda: string = '';
    mensaje: string = '';
    tipoMensaje: string = '';

    constructor(
        private cuentaService: CuentaService,
        private movimientoService: MovimientoService,
        private cdr: ChangeDetectorRef
    ) { }

	ngOnInit(): void {
        this.cargarMovimientos();
        this.cargarCuentas();
	}

    initForm(): Movimiento {
        return {
    tipoMovimiento: 'Deposito',
    valor: 0,
    cuenta: { id: 0 }
};
    }

    cargarMovimientos(): void {
        this.movimientoService.getAll().subscribe({
            next: (data) => {
                this.movimientos = [...data];
                this.movimientosFiltrados = [...data];
                this.cdr.detectChanges();
                
            },
            error: (err) => {
                this.mostrarMensaje('Error al cargar movimientos', 'error');
            }
        });
    }

    cargarCuentas(): void {
        this.cuentaService.getAll().subscribe({
            next: (data) => {
                this.cuentas = data;
                this.cdr.detectChanges();
            },
            error: (err) => {
                this.mostrarMensaje('Error al cargar cuentas', 'error');
            }
        });
    }

    buscar(): void {
        const termino = this.busqueda.toLowerCase().trim();
        if(!termino) {
            this.movimientosFiltrados = [...this.movimientos];
            this.cdr.detectChanges();
            return;
        }
        this.movimientosFiltrados = this.movimientos.filter(m =>
              Object.values(m).some(val =>
                val !== null && val !== undefined && typeof val !== 'object' && val.toString().toLowerCase().includes(termino)
            )|| m.cuenta?.numeroCuenta?.toLowerCase().includes(termino) || m.cuenta?.cliente?.nombre?.toLowerCase().includes(termino)
        );
        this.cdr.detectChanges();
    }

    abrirModal(cuenta?: Cuenta): void {
        this.movimientoForm = this.initForm();
        this.mostrarModal = true;
        this.cdr.detectChanges();
    }

    cerrarModal(): void {
        this.mostrarModal = false;
        this.movimientoForm = this.initForm();
        this.cdr.detectChanges();
    }

    guardar(): void {
        if(this.movimientoForm.cuenta?.id === 0) {
            this.mostrarMensaje('Debe seleccionar una cuenta', 'error');
            return;
        }
        if(this.movimientoForm.valor <= 0) {
            this.mostrarMensaje('El valor debe ser mayor a 0', 'error');
            return;
        }

        // Si es retiro el valor debe ser negativo
        if(this.movimientoForm.tipoMovimiento === 'Retiro') {
            this.movimientoForm.valor = -Math.abs(this.movimientoForm.valor);
        } else {
            this.movimientoForm.valor = Math.abs(this.movimientoForm.valor);
        }


        
            this.movimientoService.create(this.movimientoForm).subscribe({
                next: () => {
                    this.mostrarMensaje('Movimiento registrado correctamente', 'success');
                    this.cargarMovimientos();
                    this.cerrarModal();
                    this.cdr.detectChanges();
                },
                error: (err) => {
                    // Resetear valor a positivo para mostrar en el formulario
                    this.movimientoForm.valor = Math.abs(this.movimientoForm.valor);

                    //Capturar mensaje del backend o mostrar mensaje genérico
                    let msg = 'Error al registrar movimiento';
                    if( err.error?.message) {
                        msg = err.error.message;
                    }else if(typeof err.error === 'string'){
                        msg = err.error;
                    }
                    this.mostrarMensaje(msg, 'error');
                    this.cdr.detectChanges();
                }
            });
        
    }

    eliminar(id: number): void {
        if(confirm('¿Estás seguro de eliminar este movimiento?')) {
            this.movimientoService.delete(id).subscribe({
                next: () => {
                    this.mostrarMensaje('Movimiento eliminado correctamente', 'success');
                    this.cargarMovimientos();
                },
                error: (err) => {
                    this.mostrarMensaje('Error al eliminar movimiento', 'error');
                }
            });
        }
    }

    mostrarMensaje(mensaje: string, tipo: string): void {
        this.mensaje = mensaje;
        this.tipoMensaje = tipo;
        setTimeout(() => {
            this.mensaje = '';
            this.tipoMensaje = '';
        }, 5000);
    }
    }
