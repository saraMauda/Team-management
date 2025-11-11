import { Injectable } from '@angular/core';
import { BehaviorSubject, tap } from 'rxjs';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { API_BASE_URL } from '../app.config';

@Injectable({ providedIn: 'root' })
export class AuthService {
private _isLoggedIn = new BehaviorSubject<boolean | null>(null);
isLoggedIn$ = this._isLoggedIn.asObservable();


  constructor(private http: HttpClient, private router: Router) {
    this.checkInitialAuthState(); // בדיקה ראשונית אם יש עוגייה תקפה
  }

  /** התחברות למערכת */
/** התחברות למערכת */
signin(email: string, password: string) {
  return this.http.post<any>(`${API_BASE_URL}/users/signin`, { email, password }, { withCredentials: true })
    .pipe(
      tap({
        next: (response) => {
          console.log('✅ Sign-in successful:', response);
          this._isLoggedIn.next(true);

          // שמירה של פרטי המשתמש (אם תרצי להשתמש מאוחר יותר)
          localStorage.setItem('user', JSON.stringify(response));

          // ניתוב לפי roleString
          const role = response.role;
          if (role === 'ROLE_ADMIN') {
            this.router.navigate(['/admin-dashboard']);
          } else if (role === 'ROLE_TEAMLEADER') {
            this.router.navigate(['/leader-dashboard']);
          } else {
            this.router.navigate(['/employee-dashboard']);
          }
        },
        error: (err: any) => {
          console.error('❌ Sign-in failed:', err);
          this._isLoggedIn.next(false);
        }
      })
    );
}




  /** התנתקות */
  signOut(): void {
    this.http.post(`${API_BASE_URL}/users/signout`, {}, { withCredentials: true,responseType: 'text' })
      .subscribe({
        next: () => {
          console.log('✅ Signed out');
          this._isLoggedIn.next(false);
          this.router.navigate(['/login']);
        },
        error: (err: any) => {
          console.error('❌ Error during sign-out:', err);
          this._isLoggedIn.next(false);
          this.router.navigate(['/login']);
        }
      });
  }

  /** בדיקה אם המשתמש מחובר (נשלחת לשרת פעם אחת בהפעלה) */
checkInitialAuthState() {
  this.http.get(`${API_BASE_URL}/users/authenticated`, { withCredentials: true })
    .subscribe({
      next: () => this._isLoggedIn.next(true),
      error: () => this._isLoggedIn.next(false)
    });
}

}
