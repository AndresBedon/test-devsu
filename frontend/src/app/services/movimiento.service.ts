import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Movimiento } from '../models/movimiento.model';


@Injectable({
  providedIn: 'root'
})
export class MovimientoService {
  private baseUrl = '/api/movimientos';

  constructor(private http: HttpClient) { }

  getAll(): Observable<Movimiento[]> {
    return this.http.get<Movimiento[]>(this.baseUrl);
  }

  create(mov: Movimiento): Observable<Movimiento> {
    return this.http.post<Movimiento>(this.baseUrl, mov);
  }


  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }
}
