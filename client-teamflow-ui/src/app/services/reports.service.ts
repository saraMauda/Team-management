import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { API_BASE_URL } from '../app.config';
import { Observable } from 'rxjs';
import { ReportDTO } from '../models/report-dto.model';

@Injectable({
  providedIn: 'root'
})
export class ReportsService {

  private baseUrl = `${API_BASE_URL}/reports`;

  constructor(private http: HttpClient) { }

  getByEmployee(userId: number): Observable<any[]> {
    return this.http.get<any[]>(`${this.baseUrl}/byEmployee/${userId}`, {
      withCredentials: true
    });
  }

  addReport(report: any): Observable<any> {
    return this.http.post<any>(`${this.baseUrl}`, report, {
      withCredentials: true
    });
  }

  getReportsByLeader(leaderId: number) {
    return this.http.get<any[]>(
      `${API_BASE_URL}/reports/byLeader/${leaderId}`,
      { withCredentials: true }
    );
  }
  getAll(): Observable<ReportDTO[]> {
    return this.http.get<ReportDTO[]>(`${this.baseUrl}`, {
      withCredentials: true
    });
  }
  getComments(reportId: number) {
    return this.http.get<any[]>(
      `${API_BASE_URL}/report-comments/${reportId}`,
      { withCredentials: true }
    );
  }

  addComment(reportId: number, body: { text: string, userId: string | number }) { 
    return this.http.post<any>(
      `${API_BASE_URL}/report-comments/add/${reportId}`,
      body, 
      { withCredentials: true }
    );
  }
  updateStatus(reportId: number, status: string) {
    return this.http.put<any>(
      `${this.baseUrl}/update-status/${reportId}`,
      { status: status },
      { withCredentials: true }
    );
  }

}
