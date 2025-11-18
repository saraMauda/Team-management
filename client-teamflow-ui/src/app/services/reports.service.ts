import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { API_BASE_URL } from '../app.config';
import { Observable } from 'rxjs';

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

getComments(reportId: number) {
  return this.http.get<any[]>(
    `${API_BASE_URL}/report-comments/${reportId}`,
    { withCredentials: true }
  );
}

addComment(reportId: number, body: { text: string }) {
  return this.http.post<any>(
    `${API_BASE_URL}/report-comments/add/${reportId}`,
    body,
    { withCredentials: true }
  );
}

}
