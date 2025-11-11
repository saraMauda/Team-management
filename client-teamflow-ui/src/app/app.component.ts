import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { AuthService } from './services/auth.service';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet],
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {
  title = 'TeamFlow';

  constructor(private authService: AuthService) {
    // 🔹 נוודא שהמערכת בודקת אם המשתמש עדיין מחובר ברגע שטוענים את האפליקציה
    this.authService.checkInitialAuthState();
  }
}
