import { Component, EventEmitter, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { UsersService } from '../../../services/users.service';
import { AuthService } from '../../../services/auth.service';

@Component({
  selector: 'app-change-password-modal',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './change-password-modal.component.html',
  styleUrls: ['./change-password-modal.component.css']
})
export class ChangePasswordModalComponent {

  @Output() close = new EventEmitter<void>();

  oldPassword = '';
  newPassword = '';
  confirmPassword = '';
  message = '';

  userId: number | null = null;

  constructor(
    private usersService: UsersService,
    private authService: AuthService
  ) {}

  ngOnInit() {
    this.loadUserId();
  }

  /** ✔ מביאים את ה-ID מהשרת לפי אימייל */
  loadUserId() {
    const email = this.authService.getCurrentUserEmail();
    if (!email) {
      this.message = "User not authenticated";
      return;
    }

    this.usersService.getByEmail(email).subscribe({
      next: (user) => {
        this.userId = user.id; // ⭐ פה ID אמיתי
      },
      error: () => {
        this.message = "Cannot load user info";
      }
    });
  }

  cancel() {
    this.close.emit();
  }

  save() {
    if (!this.oldPassword || !this.newPassword || !this.confirmPassword) {
      this.message = "All fields are required";
      return;
    }

    if (this.newPassword !== this.confirmPassword) {
      this.message = "New passwords do not match";
      return;
    }

    this.submit();
  }

  /** ✔ שליחת הבקשה לשרת */
  submit() {
  const email = this.authService.getCurrentUserEmail();
  if (!email) {
    this.message = "User not authenticated";
    return;
  }

  this.usersService.getByEmail(email).subscribe({
    next: (user) => {
      const userId = user.id;

      this.usersService.changePassword(userId, this.oldPassword, this.newPassword)
        .subscribe({
          next: () => {
            this.message = "Password updated successfully!";

            // 🔥 מבצעים Logout מיד — בלי לחכות לבקשה נוספת
            this.forceLogout();
          },
          error: (err) => {
            this.message = err.error || "Error updating password";
          }
        });
    },
    error: () => {
      this.message = "Could not load user info";
    }
  });
}

forceLogout() {
  // 🔥 חשוב!!! לסגור מודל לפני ה־Logout
  this.close.emit();

  // מוחקים לוקל סטורג' שלא יישאר "תקוע"
  localStorage.removeItem('user');

  // 🔥 ניתוב ישיר — לא מחכים לתשובה מהשרת בכלל
  this.authService.signOut();

  // fallback ביטוח למקרה שהשרת לא יחזיר תשובה
  setTimeout(() => {
    this.authService.navigateToLogin();
  }, 300);
}

}
