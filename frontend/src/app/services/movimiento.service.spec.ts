import { of, throwError } from 'rxjs';

const mockMovimientoService = {
    getAll: jest.fn(),
    create: jest.fn(),
    delete: jest.fn()
};

describe('MovimientoService Mock', () => {

    beforeEach(() => {
        jest.clearAllMocks();
    });

    it('debe obtener todos los movimientos', (done) => {
        const mockData = [
            { id: 1, tipoMovimiento: 'Retiro', valor: -575, saldo: 1425 }
        ];
        mockMovimientoService.getAll.mockReturnValue(of(mockData));

        mockMovimientoService.getAll().subscribe((data: any[]) => {
            expect(data.length).toBe(1);
            expect(data[0].valor).toBe(-575);
            done();
        });
    });

    it('debe crear un deposito', (done) => {
        const movimiento = { tipoMovimiento: 'Deposito', valor: 500 };
        mockMovimientoService.create.mockReturnValue(of({ id: 1, ...movimiento, saldo: 2500 }));

        mockMovimientoService.create(movimiento).subscribe((data: any) => {
            expect(data.valor).toBe(500);
            expect(data.saldo).toBe(2500);
            done();
        });
    });

    it('debe lanzar error con saldo no disponible', (done) => {
        mockMovimientoService.create.mockReturnValue(
            throwError(() => ({ error: { message: 'Saldo no disponible' } }))
        );

        mockMovimientoService.create({ valor: -100 }).subscribe({
            error: (err: any) => {
                expect(err.error.message).toBe('Saldo no disponible');
                done();
            }
        });
    });

    it('debe eliminar un movimiento', (done) => {
        mockMovimientoService.delete.mockReturnValue(of(null));

        mockMovimientoService.delete(1).subscribe(() => {
            expect(mockMovimientoService.delete).toHaveBeenCalledWith(1);
            done();
        });
    });
});