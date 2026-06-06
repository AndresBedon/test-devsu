import { CommonModule } from '@angular/common';
import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ClienteService } from '../../services/cliente.service';
import { Cliente } from '../../models/cliente.model';



@Component({
	selector: 'app-clientes',
    standalone: true,
    imports: [CommonModule, FormsModule],
	templateUrl: './clientes.component.html',
    styleUrl: './clientes.component.css'
})
export class ClientesComponent implements OnInit {
	clientes: Cliente[] = [];
    clientesFiltrados: Cliente[] = [];
    clienteForm: Cliente = this.initForm();
    editando: boolean = false;
    mostrarModal: boolean = false;
    busqueda: string = '';
    mensaje: string = '';
    tipoMensaje: string = '';

    constructor(
        private clienteService: ClienteService,
        private cdr: ChangeDetectorRef
    ) { }

	ngOnInit(): void {
        this.cargarClientes();
	}

    initForm(): Cliente {
        return {
            nombre:'',
            genero:'',
            edad:0,
            identificacion:'',
            direccion:'',
            telefono:'',
            clienteId:'',
            contrasena:'',
            estado:true
        };
    }

    cargarClientes(): void {
        this.clienteService.getAll().subscribe({
            next: (data) => {
                this.clientes = [...data];
                this.clientesFiltrados = [...data];
                this.cdr.detectChanges();
                
            },
            error: (err) => {
                this.mostrarMensaje('Error al cargar clientes', 'error');
            }
        });
    }

    buscar(): void {
        const termino = this.busqueda.toLowerCase();
        this.clientesFiltrados = this.clientes.filter(c =>
            c.nombre.toLowerCase().includes(termino) ||
            c.identificacion.toLowerCase().includes(termino) ||
            c.telefono.toLowerCase().includes(termino)
        );
        this.cdr.detectChanges();
    }

    abrirModal(cliente?: Cliente): void {
        this.editando = !!cliente;
        this.clienteForm = cliente ? { ...cliente } : this.initForm();
        this.mostrarModal = true;
        this.cdr.detectChanges();
    }

    cerrarModal(): void {
        this.mostrarModal = false;
        this.clienteForm = this.initForm();
        this.cdr.detectChanges();
    }

    guardar(): void {
        if(this.editando && this.clienteForm.id) {
            this.clienteService.update(this.clienteForm.id, this.clienteForm).subscribe({
                next: () => {
                    this.mostrarMensaje('Cliente actualizado correctamente', 'success');
                    this.cargarClientes();
                    this.cerrarModal();
                },
                error: (err) => {
                    this.mostrarMensaje('Error al actualizar cliente', 'error');
                }
            });
        } else {
            this.clienteService.create(this.clienteForm).subscribe({
                next: () => {
                    this.mostrarMensaje('Cliente creado correctamente', 'success');
                    this.cargarClientes();
                    this.cerrarModal();
                },
                error: (err) => {
                    this.mostrarMensaje('Error al crear cliente', 'error');
                }
            });
        }
    }

    eliminar(id: number): void {
        if(confirm('¿Estás seguro de eliminar este cliente?')) {
            this.clienteService.delete(id).subscribe({
                next: () => {
                    this.mostrarMensaje('Cliente eliminado correctamente', 'success');
                    this.cargarClientes();
                },
                error: (err) => {
                    this.mostrarMensaje('Error al eliminar cliente', 'error');
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
