import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ProjectDTO } from '../models/project-dto.model';

@Injectable({
  providedIn: 'root'
})
export class ProjectsService {
  private API = 'http://localhost:8080/api/projects';

  constructor(private http: HttpClient) {}

  getAll(): Observable<ProjectDTO[]> {
    return this.http.get<ProjectDTO[]>(`${this.API}`);
  }

  getById(id: number): Observable<ProjectDTO> {
    return this.http.get<ProjectDTO>(`${this.API}/${id}`);
  }

  create(project: Partial<ProjectDTO>): Observable<ProjectDTO> {
    return this.http.post<ProjectDTO>(`${this.API}`, project);
  }

  update(id: number, project: Partial<ProjectDTO>): Observable<ProjectDTO> {
    return this.http.put<ProjectDTO>(`${this.API}/${id}`, project);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.API}/${id}`);
  }

  getMyProjects(): Observable<ProjectDTO[]> {
    return this.http.get<ProjectDTO[]>(`${this.API}/byEmployee`);
  }

  getByLeader(id: number) {
  return this.http.get<ProjectDTO[]>(`${this.API}/byLeader/${id}`, {
    withCredentials: true
  });
}

}
