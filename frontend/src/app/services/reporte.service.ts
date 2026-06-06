import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
	providedIn: 'root'
})
export class ReporteService {
	private  baseUrl = '/api/reportes';

	constructor(private  http: HttpClient) {}

	getReporte(
        clienteId: number,
        fechaInicio: string,
        fechaFin: string
    ): Observable<any> {
		return this.http.get<any>(`${this.baseUrl}?clienteId=${clienteId}&fechaInicio=${fechaInicio}&fechaFin=${fechaFin}`);
	}
}
