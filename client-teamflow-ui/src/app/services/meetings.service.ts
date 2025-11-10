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

  getAll(): Observable<MeetingDTO[]> {
    return this.http.get<MeetingDTO[]>(this.baseUrl, { withCredentials: true });
  }

  getById(id: number): Observable<MeetingDTO> {
    return this.http.get<MeetingDTO>(`${this.baseUrl}/${id}`, { withCredentials: true });
  }

  create(meeting: Partial<MeetingDTO>): Observable<MeetingDTO> {
    return this.http.post<MeetingDTO>(this.baseUrl, meeting, { withCredentials: true });
  }

  update(id: number, meeting: Partial<MeetingDTO>): Observable<MeetingDTO> {
    return this.http.put<MeetingDTO>(`${this.baseUrl}/${id}`, meeting, { withCredentials: true });
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`, { withCredentials: true });
  }
}
