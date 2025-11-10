// src/app/services/users.service.ts
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { UsersDTO } from '../models/users-dto.model';
import { API_BASE_URL } from '../app.config';

@Injectable({
  providedIn: 'root'
})
export class UsersService {
  // 👇 נתיב בסיס — שימי לב: "/api/users"
  private baseUrl = `${API_BASE_URL}/users`;

  constructor(private http: HttpClient) {}

  // 📥 שליפת כל המשתמשים
  getAll(): Observable<UsersDTO[]> {
    return this.http.get<UsersDTO[]>(this.baseUrl, { withCredentials: true });
  }

  // 📥 שליפת משתמש לפי ID
  getById(id: number): Observable<UsersDTO> {
    return this.http.get<UsersDTO>(`${this.baseUrl}/get/${id}`, { withCredentials: true });
  }

  // 🆕 יצירת משתמש חדש
  // חשוב! בשרת ה-endpoint נקרא /signup
  create(user: Partial<UsersDTO>): Observable<UsersDTO> {
    return this.http.post<UsersDTO>(`${this.baseUrl}/signup`, user, { withCredentials: true });
  }

  // ✏️ עדכון משתמש קיים
  update(id: number, user: Partial<UsersDTO>): Observable<UsersDTO> {
    return this.http.put<UsersDTO>(`${this.baseUrl}/${id}`, user, { withCredentials: true });
  }

  // ❌ מחיקת משתמש לפי ID
  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`, { withCredentials: true });
  }

  // 📸 העלאת תמונה למשתמש מסוים
  uploadImage(id: number, file: File): Observable<string> {
    const formData = new FormData();
    formData.append('image', file);

    return this.http.post(`${this.baseUrl}/upload/${id}`, formData, {
      responseType: 'text' // השרת מחזיר רק מחרוזת (לא JSON)
    });
  }
}
