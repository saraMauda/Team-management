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

  constructor(private http: HttpClient) {}

  /** ✔ דוחות של עובד */
  getByEmployee(userId: number): Observable<any[]> {
    return this.http.get<any[]>(`${this.baseUrl}/byEmployee/${userId}`, {
      withCredentials: true
    });
  }

  /** ✔ דוחות של ראש צוות (עובדים בצוות שלו) */

  /** ✔ הוספת דוח חדש */
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

// src/app/services/reports.service.ts
// ... (שאר הקוד) ...

addComment(reportId: number, body: { text: string, userId: string | number }) { // ⭐ הוספת userId לטיפוס ⭐
  return this.http.post<any>(
    `${API_BASE_URL}/report-comments/add/${reportId}`,
    body, // עכשיו body מכיל { text: string, userId: string|number }
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
