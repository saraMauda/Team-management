// src/app/services/meetings.service.ts
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { MeetingDTO } from '../models/meeting-dto.model';
import { API_BASE_URL } from '../app.config';

@Injectable({
  providedIn: 'root'
})
export class MeetingsService {
  private baseUrl = `${API_BASE_URL}/meetings`;

  constructor(private http: HttpClient) {}

  // --- Admin: get all meetings ---
  getAll(): Observable<MeetingDTO[]> {
    return this.http.get<MeetingDTO[]>(this.baseUrl, {
      withCredentials: true
    });
  }

  // --- Get single meeting by id ---
  getById(id: number): Observable<MeetingDTO> {
    return this.http.get<MeetingDTO>(`${this.baseUrl}/${id}`, {
      withCredentials: true
    });
  }

  // --- Team Leader: create meeting ---
  create(meeting: Partial<MeetingDTO>): Observable<MeetingDTO> {
    return this.http.post<MeetingDTO>(`${this.baseUrl}/create`, meeting, {
      withCredentials: true
    });
  }

  // --- Admin / Team Leader: update meeting ---
  update(id: number, meeting: Partial<MeetingDTO>): Observable<MeetingDTO> {
    return this.http.put<MeetingDTO>(`${this.baseUrl}/${id}`, meeting, {
      withCredentials: true
    });
  }

  // --- Admin / Team Leader: delete meeting ---
  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`, {
      withCredentials: true
    });
  }

  // --- Employee / Team Leader: my meetings ---
  getMyMeetings(): Observable<MeetingDTO[]> {
    return this.http.get<MeetingDTO[]>(
      `${API_BASE_URL}/meetings/my`,
      { withCredentials: true }
    );
  }

  // --- Team Leader: meetings of specific project ---
  getTeamMeetings(projectId: number): Observable<MeetingDTO[]> {
    return this.http.get<MeetingDTO[]>(
      `${API_BASE_URL}/meetings/team/${projectId}`,
      { withCredentials: true }
    );
  }
}
