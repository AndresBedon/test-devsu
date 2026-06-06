import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Cuenta } from '../models/cuenta.model';



@Injectable({
	providedIn: 'root',
})
export class CuentaService {
	private readonly baseUrl = '/api/cuentas';

	constructor(private readonly http: HttpClient) {}

	getAll(): Observable<Cuenta[]> {
		return this.http.get<Cuenta[]>(this.baseUrl);
	}

	getById(id: number): Observable<Cuenta> {
		return this.http.get<Cuenta>(`${this.baseUrl}/${id}`);
	}

	create(cuenta: Cuenta): Observable<Cuenta> {
		return this.http.post<Cuenta>(this.baseUrl, cuenta);
	}

	update(id: number, cuenta: Cuenta): Observable<Cuenta> {
		return this.http.put<Cuenta>(`${this.baseUrl}/${id}`, cuenta);
	}

	delete(id: number): Observable<void> {
		return this.http.delete<void>(`${this.baseUrl}/${id}`);
	}
}
