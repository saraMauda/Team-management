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

  /** ✔ כל המשתמשים */
  getAllUsers(): Observable<UsersDTO[]> {
    return this.http.get<UsersDTO[]>(this.baseUrl, { withCredentials: true });
  }

  /** ✔ לפי ID */
  getById(id: number): Observable<UsersDTO> {
    return this.http.get<UsersDTO>(`${this.baseUrl}/get/${id}`, {
      withCredentials: true
    });
  }

  /** ✔ לפי אימייל (משמש להוצאת ה-ID מהשרת) */
  getByEmail(email: string) {
    return this.http.get<UsersDTO>(`${this.baseUrl}/by-email/${email}`, {
      withCredentials: true
    });
  }

  /** ✔ שינוי סיסמה */
  changePassword(id: number, oldPassword: string, newPassword: string) {
    return this.http.put(
      `${this.baseUrl}/change-password/${id}`,
      {
        oldPassword: oldPassword,
        newPassword: newPassword
      },
      { withCredentials: true }
    );
  }

  /** ✔ יצירת משתמש */
  create(user: Partial<UsersDTO>): Observable<UsersDTO> {
    return this.http.post<UsersDTO>(`${this.baseUrl}/signup`, user, {
      withCredentials: true
    });
  }

  /** ✔ עדכון משתמש */
  update(id: number, user: Partial<UsersDTO>): Observable<UsersDTO> {
    return this.http.put<UsersDTO>(`${this.baseUrl}/${id}`, user, {
      withCredentials: true
    });
  }

  /** ✔ מחיקת משתמש */
  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`, {
      withCredentials: true
    });
  }

  /** ✔ העלאת תמונה */
  uploadImage(id: number, file: File): Observable<string> {
    const formData = new FormData();
    formData.append('image', file);

    return this.http.post(`${this.baseUrl}/upload/${id}`, formData, {
      withCredentials: true,
      responseType: 'text'
    });
  }
}
