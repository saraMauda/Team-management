import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Approval } from '../models/approval.model';

@Injectable({
  providedIn: 'root'
})
export class ApprovalService {

  private baseUrl = 'http://localhost:8080/api/approvals';

  constructor(private http: HttpClient) {}

  getApprovalsByMeeting(meetingId: number): Observable<Approval[]> {
    return this.http.get<Approval[]>(`${this.baseUrl}/meeting/${meetingId}`);
  }

  createApproval(approval: any): Observable<Approval> {
    return this.http.post<Approval>(`${this.baseUrl}`, approval);
  }
}
