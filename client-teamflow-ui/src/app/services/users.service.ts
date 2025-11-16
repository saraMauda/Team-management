import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { UsersDTO } from '../models/users-dto.model';
import { API_BASE_URL } from '../app.config';

@Injectable({
  providedIn: 'root'
})
export class UsersService {

  private baseUrl = `${API_BASE_URL}/users`;

  constructor(private http: HttpClient) {}

  // ✔ שינוי כדי להתאים ל־Manage Users
  getAllUsers(): Observable<UsersDTO[]> {
    return this.http.get<UsersDTO[]>(this.baseUrl, { withCredentials: true });
  }

  getById(id: number): Observable<UsersDTO> {
    return this.http.get<UsersDTO>(`${this.baseUrl}/get/${id}`, { withCredentials: true });
  }
  getByEmail(email: string) {
  return this.http.get<any>(`${API_BASE_URL}/users/by-email/${email}`, { withCredentials: true });
}


  create(user: Partial<UsersDTO>): Observable<UsersDTO> {
    return this.http.post<UsersDTO>(`${this.baseUrl}/signup`, user, { withCredentials: true });
  }

  update(id: number, user: Partial<UsersDTO>): Observable<UsersDTO> {
    return this.http.put<UsersDTO>(`${this.baseUrl}/${id}`, user, { withCredentials: true });
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`, { withCredentials: true });
  }

  uploadImage(id: number, file: File): Observable<string> {
    const formData = new FormData();
    formData.append('image', file);

    return this.http.post(`${this.baseUrl}/upload/${id}`, formData, {
      responseType: 'text'
    });
  }
}
