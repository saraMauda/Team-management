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

  /** 🔹 מחזיר את כל הפרויקטים (ל־Admin) */
  getAll(): Observable<ProjectDTO[]> {
    return this.http.get<ProjectDTO[]>(`${this.API}`);
  }

  /** 🔹 מחזיר פרויקט לפי מזהה */
  getById(id: number): Observable<ProjectDTO> {
    return this.http.get<ProjectDTO>(`${this.API}/${id}`);
  }

  /** 🔹 יצירת פרויקט */
  create(project: Partial<ProjectDTO>): Observable<ProjectDTO> {
    return this.http.post<ProjectDTO>(`${this.API}`, project);
  }

  /** 🔹 עדכון פרויקט */
  update(id: number, project: Partial<ProjectDTO>): Observable<ProjectDTO> {
    return this.http.put<ProjectDTO>(`${this.API}/${id}`, project);
  }

  /** 🔹 מחיקת פרויקט */
  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.API}/${id}`);
  }

  /** 🔹 פרויקטים של עובד מחובר */
  getMyProjects(): Observable<ProjectDTO[]> {
    return this.http.get<ProjectDTO[]>(`${this.API}/byEmployee`);
  }

  /** 🔹 פרויקטים של ראש צוות */
  getByLeader(leaderId: number): Observable<ProjectDTO[]> {
    return this.http.get<ProjectDTO[]>(`${this.API}/byLeader/${leaderId}`);
  }
}
