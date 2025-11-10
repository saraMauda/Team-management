import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Category } from '../models/category.model';
import { API_BASE_URL } from '../app.config';

@Injectable({ providedIn: 'root' })
export class CategoriesService {
  private baseUrl = `${API_BASE_URL}/categories`;

  constructor(private http: HttpClient) {}

  getAll(): Observable<Category[]> {
    return this.http.get<Category[]>(this.baseUrl, { withCredentials: true });
  }

  getById(id: number): Observable<Category> {
    return this.http.get<Category>(`${this.baseUrl}/${id}`, { withCredentials: true });
  }

  create(category: Partial<Category>): Observable<Category> {
    return this.http.post<Category>(this.baseUrl, category, { withCredentials: true });
  }

  update(id: number, category: Partial<Category>): Observable<Category> {
    return this.http.put<Category>(`${this.baseUrl}/${id}`, category, { withCredentials: true });
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`, { withCredentials: true });
  }
}
