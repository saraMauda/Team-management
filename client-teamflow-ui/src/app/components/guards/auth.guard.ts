import { Injectable } from '@angular/core';
import { CanActivate, Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { Observable } from 'rxjs';
import { map, filter, take } from 'rxjs/operators';

@Injectable({ providedIn: 'root' })
export class AuthGuard implements CanActivate {
  constructor(private authService: AuthService, private router: Router) {}

  canActivate(): Observable<boolean> {
    // נמתין עד שה־BehaviorSubject באמת מקבל ערך (true/false)
    return this.authService.isLoggedIn$.pipe(
      filter(val => val !== null), // לוודא שהערך כבר מאותחל
      take(1),
      map(isLoggedIn => {
        if (isLoggedIn) {
          return true; // ✅ המשתמש מחובר
        } else {
          this.router.navigate(['/login']);
          return false;
        }
      })
    );
  }
}
