import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ProjectDTO } from '../models/project-dto.model';
import { API_BASE_URL } from '../app.config';

@Injectable({
  providedIn: 'root'
})
export class ProjectsService {

  private API = `${API_BASE_URL}/projects`;

  constructor(private http: HttpClient) {}

  getAll(): Observable<ProjectDTO[]> {
    return this.http.get<ProjectDTO[]>(`${this.API}`, {
      withCredentials: true
    });
  }

  getById(id: number): Observable<ProjectDTO> {
    return this.http.get<ProjectDTO>(`${this.API}/${id}`, {
      withCredentials: true
    });
  }

  create(project: Partial<ProjectDTO>): Observable<ProjectDTO> {
    return this.http.post<ProjectDTO>(`${this.API}`, project, {
      withCredentials: true
    });
  }

  update(id: number, project: Partial<ProjectDTO>): Observable<ProjectDTO> {
    return this.http.put<ProjectDTO>(`${this.API}/${id}`, project, {
      withCredentials: true
    });
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.API}/${id}`, {
      withCredentials: true
    });
  }

  getMyProjects(): Observable<ProjectDTO[]> {
    return this.http.get<ProjectDTO[]>(`${this.API}/byEmployee`, {
      withCredentials: true
    });
  }

  getByLeader(id: number): Observable<ProjectDTO[]> {
    return this.http.get<ProjectDTO[]>(`${this.API}/byLeader/${id}`, {
      withCredentials: true
    });
  }
}
