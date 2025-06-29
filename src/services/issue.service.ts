import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface Issue {
  id: number;
  dni: string;
  nombres: string;
  apellidoPaterno: string;
  apellidoMaterno: string;
  codVerifica: string;
  status: string; 
}

@Injectable({
  providedIn: 'root',
})
export class IssueService {
  private apiUrl = 'https://zany-rotary-phone-pvxx9rxx5pqfj9x-8080.app.github.dev';

  constructor(private http: HttpClient) {}

  getAllDniRecords(): Observable<Issue[]> {
    return this.http.get<Issue[]>(`${this.apiUrl}/all`);
  }

  getIssueById(id: number): Observable<Issue> {
    return this.http.get<Issue>(`${this.apiUrl}/${id}`);
  }

  restoreDni(id: number): Observable<string> {
    return this.http.put<string>(
      `${this.apiUrl}/restore/${id}`,
      {},
      { responseType: 'text' as 'json' }
    );
  }

  deleteDni(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/delete/${id}`);
  }

  consultDni(dni: string): Observable<void> {
    return this.http.post<void>(`${this.apiUrl}/consultar?dni=${dni}`, {});
  }
  

  updateDni(id: number, dni: string): Observable<void> {
    const url = `${this.apiUrl}/update/${id}`;
    return this.http.put<void>(url, null, { params: { dni } });
  }
  

  deleteLogical(id: number): Observable<string> {
    return this.http.delete(`${this.apiUrl}/delete/${id}`, {
      responseType: 'text',
    });
  }

}
