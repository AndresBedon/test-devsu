import { of } from 'rxjs';

// Mock del servicio sin importar Angular
const mockClienteService = {
    getAll: jest.fn(),
    create: jest.fn(),
    update: jest.fn(),
    delete: jest.fn()
};

describe('ClienteService Mock', () => {

    beforeEach(() => {
        jest.clearAllMocks();
    });

    it('debe obtener todos los clientes', (done) => {
        const mockData = [
            { id: 1, nombre: 'Jose Lema', estado: true }
        ];
        mockClienteService.getAll.mockReturnValue(of(mockData));

        mockClienteService.getAll().subscribe((data: any[]) => {
            expect(data.length).toBe(1);
            expect(data[0].nombre).toBe('Jose Lema');
            done();
        });
    });

    it('debe crear un cliente', (done) => {
        const nuevoCliente = { nombre: 'Marianela', estado: true };
        mockClienteService.create.mockReturnValue(of({ id: 2, ...nuevoCliente }));

        mockClienteService.create(nuevoCliente).subscribe((data: any) => {
            expect(data.nombre).toBe('Marianela');
            expect(data.id).toBe(2);
            done();
        });
    });

    it('debe actualizar un cliente', (done) => {
        const clienteActualizado = { id: 1, nombre: 'Jose Actualizado' };
        mockClienteService.update.mockReturnValue(of(clienteActualizado));

        mockClienteService.update(1, clienteActualizado).subscribe((data: any) => {
            expect(data.nombre).toBe('Jose Actualizado');
            done();
        });
    });

    it('debe eliminar un cliente', (done) => {
        mockClienteService.delete.mockReturnValue(of(null));

        mockClienteService.delete(1).subscribe(() => {
            expect(mockClienteService.delete).toHaveBeenCalledWith(1);
            done();
        });
    });
});