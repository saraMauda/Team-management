import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class EmployeeProjectService {

  private baseUrl = 'http://localhost:8080/api/employeeProject';

  constructor(private http: HttpClient) {}

  /** שליפת עובדים ששייכים למנהל צוות */
  getEmployeesForLeader(leaderId: number): Observable<any[]> {
    return this.http.get<any[]>(`${this.baseUrl}/employeesByLeader/${leaderId}`);
  }

  /** שיבוץ עובד לפרויקט */
  assignEmployeeToProject(userId: number, projectId: number): Observable<any> {
    return this.http.post(`${this.baseUrl}/assign`, { userId, projectId });
  }
}
