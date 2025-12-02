import { Component, OnInit, Output, EventEmitter } from '@angular/core';
import { UsersService } from '../../../services/users.service';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-change-password-modal',
  standalone: true,
  templateUrl: './change-password-modal.component.html',
  styleUrls: ['./change-password-modal.component.css'],
  imports: [CommonModule, FormsModule]
})
export class ChangePasswordModalComponent implements OnInit {

  @Output() close = new EventEmitter<void>();

  oldPassword = '';
  newPassword = '';
  confirmPassword = '';

  userId: number = 0;

  errorMessage = '';
  successMessage = '';
  loading = false;

  constructor(private usersService: UsersService) {}

  ngOnInit(): void {
    this.loadUser();
  }

  /** -----------------------------------------
   * שליפת userId מה־localStorage (כמו HEADER)
   * ----------------------------------------- */
  loadUser() {
    const stored = localStorage.getItem("user");

    if (!stored) {
      this.errorMessage = "Unable to find user session";
      return;
    }

    try {
      const parsed = JSON.parse(stored);
      this.userId = parsed.id;       // ← הכי חשוב
    } catch {
      this.errorMessage = "Session error";
    }
  }

  /** ולידציה בסיסית */
  validateNewPassword(): string | null {
    if (this.newPassword.length < 8) {
      return "Password must be at least 8 characters";
    }

    const letters = this.newPassword.split("").filter(ch => /[a-zA-Z]/.test(ch));
    if (letters.length < 2) {
      return "Password must contain at least 2 English letters";
    }

    return null;
  }

  /** שינוי סיסמה */
  changePassword() {
    this.errorMessage = '';
    this.successMessage = '';

    if (this.newPassword !== this.confirmPassword) {
      this.errorMessage = "Passwords do not match";
      return;
    }

    const valid = this.validateNewPassword();
    if (valid) {
      this.errorMessage = valid;
      return;
    }

    if (!this.userId) {
      this.errorMessage = "User not loaded";
      return;
    }

    this.loading = true;

    this.usersService.changePassword(this.userId, this.oldPassword, this.newPassword)
      .subscribe({
        next: () => {
          this.loading = false;
          this.successMessage = "Password updated successfully";

          setTimeout(() => this.closeModal(), 1200);
        },
        error: (err) => {
          this.loading = false;
          this.errorMessage = err.error?.message || "Error updating password";
        }
      });
  }

  closeModal() {
    this.close.emit();
  }
}
