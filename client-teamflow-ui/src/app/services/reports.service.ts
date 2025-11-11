// src/app/services/reports.service.ts
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ReportDTO } from '../models/report-dto.model';
import { API_BASE_URL } from '../app.config';

@Injectable({
  providedIn: 'root'
})
export class ReportsService {
  private baseUrl = `${API_BASE_URL}/reports`;

  constructor(private http: HttpClient) {}

  getAll(): Observable<ReportDTO[]> {
    return this.http.get<ReportDTO[]>(this.baseUrl, { withCredentials: true });
  }

  getById(id: number): Observable<ReportDTO> {
    return this.http.get<ReportDTO>(`${this.baseUrl}/${id}`, { withCredentials: true });
  }

  // create(report: Partial<ReportDTO>): Observable<ReportDTO> {
  //   return this.http.post<ReportDTO>(this.baseUrl, report, { withCredentials: true });
  // }
  create(report: any) {
  return this.http.post(`${API_BASE_URL}/reports`, report, { withCredentials: true });
}


  update(id: number, report: Partial<ReportDTO>): Observable<ReportDTO> {
    return this.http.put<ReportDTO>(`${this.baseUrl}/${id}`, report, { withCredentials: true });
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`, { withCredentials: true });
  }
  getMyReports() {
  return this.http.get<any[]>(`${API_BASE_URL}/reports/byEmployee`, {
    withCredentials: true
  });
}

}
