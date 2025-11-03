// src/app/services/projects.service.ts
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ProjectDTO } from '../models/project-dto.model';
import { API_BASE_URL } from '../app.config';

@Injectable({
  providedIn: 'root'
})
export class ProjectsService {
  private baseUrl = `${API_BASE_URL}/projects`;

  constructor(private http: HttpClient) {}

  getAll(): Observable<ProjectDTO[]> {
    return this.http.get<ProjectDTO[]>(this.baseUrl);
  }

  getById(id: number): Observable<ProjectDTO> {
    return this.http.get<ProjectDTO>(`${this.baseUrl}/${id}`);
  }

  create(project: Partial<ProjectDTO>): Observable<ProjectDTO> {
    return this.http.post<ProjectDTO>(this.baseUrl, project);
  }

  update(id: number, project: Partial<ProjectDTO>): Observable<ProjectDTO> {
    return this.http.put<ProjectDTO>(`${this.baseUrl}/${id}`, project);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }
}
