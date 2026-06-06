import { CommonModule } from '@angular/common';
import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ClienteService } from '../../services/cliente.service';
import { Cuenta } from '../../models/cuenta.model';
import { Cliente } from '../../models/cliente.model';
import { CuentaService } from '../../services/cuenta.service';



@Component({
	selector: 'app-cuentas',
    standalone: true,
    imports: [CommonModule, FormsModule],
	templateUrl: './cuentas.component.html',
    styleUrl: './cuentas.component.css'
})
export class CuentasComponent implements OnInit {
	cuentas: Cuenta[] = [];
    cuentasFiltradas: Cuenta[] = [];
    clientes: Cliente[] = [];
    cuentaForm: Cuenta = this.initForm();
    editando: boolean = false;
    mostrarModal: boolean = false;
    busqueda: string = '';
    mensaje: string = '';
    tipoMensaje: string = '';

    constructor(
        private cuentaService: CuentaService,
        private clienteService: ClienteService,
        private cdr: ChangeDetectorRef
    ) { }

	ngOnInit(): void {
        this.cargarCuentas();
        this.cargarClientes();
	}

    initForm(): Cuenta {
        return {
    numeroCuenta: '',
    tipoCuenta: 'Ahorro',
    saldoInicial: 0,
    estado: true,
    cliente: { id: 0 }
};
    }

    cargarCuentas(): void {
        this.cuentaService.getAll().subscribe({
            next: (data) => {
                this.cuentas = [...data];
                this.cuentasFiltradas = [...data];
                this.cdr.detectChanges();
                
            },
            error: (err) => {
                this.mostrarMensaje('Error al cargar cuentas', 'error');
            }
        });
    }

    cargarClientes(): void {
        this.clienteService.getAll().subscribe({
            next: (data) => {
                this.clientes = data;
                this.cdr.detectChanges();
            },
            error: (err) => {
                this.mostrarMensaje('Error al cargar clientes', 'error');
            }
        });
    }

    buscar(): void {
        const termino = this.busqueda.toLowerCase();
        this.cuentasFiltradas = this.cuentas.filter(c =>
            c.numeroCuenta.toLowerCase().includes(termino) ||
            c.tipoCuenta.toLowerCase().includes(termino) ||
            (c.clienteNombre?.toLowerCase().includes(termino) ?? false)
        );
        this.cdr.detectChanges();
    }

    abrirModal(cuenta?: Cuenta): void {
        this.editando = !!cuenta;
        if(cuenta) {
        this.cuentaForm = {
            ...cuenta,
            cliente: { id: cuenta?.clienteId! }
        }
    } else {
        this.cuentaForm = this.initForm();
    }
        this.mostrarModal = true;
        this.cdr.detectChanges();
    }

    cerrarModal(): void {
        this.mostrarModal = false;
        this.cuentaForm = this.initForm();
        this.cdr.detectChanges();
    }

    guardar(): void {
        if(this.editando && this.cuentaForm.id) {
            this.cuentaService.update(this.cuentaForm.id, this.cuentaForm).subscribe({
                next: () => {
                    this.mostrarMensaje('Cuenta actualizada correctamente', 'success');
                    this.cargarCuentas();
                    this.cerrarModal();
                },
                error: (err) => {
                    this.mostrarMensaje('Error al actualizar cuenta', 'error');
                }
            });
        } else {
            this.cuentaService.create(this.cuentaForm).subscribe({
                next: () => {
                    this.mostrarMensaje('Cuenta creada correctamente', 'success');
                    this.cargarCuentas();
                    this.cerrarModal();
                },
                error: (err) => {
                    this.mostrarMensaje('Error al crear cuenta', 'error');
                }
            });
        }
    }

    eliminar(id: number): void {
        if(confirm('¿Estás seguro de eliminar esta cuenta?')) {
            this.cuentaService.delete(id).subscribe({
                next: () => {
                    this.mostrarMensaje('Cuenta eliminada correctamente', 'success');
                    this.cargarCuentas();
                },
                error: (err) => {
                    this.mostrarMensaje('Error al eliminar cuenta', 'error');
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
        }, 3000);
    }
    }
