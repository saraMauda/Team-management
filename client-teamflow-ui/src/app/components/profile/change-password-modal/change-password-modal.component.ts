import { Component, OnInit, Output, EventEmitter } from '@angular/core';
import { UsersService } from '../../../services/users.service';
import { AuthService } from '../../../services/auth.service';
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

  constructor(
    private usersService: UsersService,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    this.loadUser();
  }

  /**
   * Loads the user ID using the email stored in localStorage.
   * This replaces the old cookie-reading logic.
   */
  loadUser() {
    const email = this.authService.getCurrentUserEmail();

    if (!email) {
      this.errorMessage = 'Unable to find user session';
      return;
    }

    this.usersService.getByEmail(email).subscribe({
      next: (user) => {
        this.userId = user.id;
      },
      error: () => {
        this.errorMessage = 'Unable to load user data';
      }
    });
  }

  validateNewPassword(): string | null {
    if (this.newPassword.length < 8) {
      return 'Password must be at least 8 characters';
    }

    const letters = this.newPassword.split('').filter(ch => /[a-zA-Z]/.test(ch));
    if (letters.length < 2) {
      return 'Password must contain at least 2 English letters';
    }

    return null;
  }

  /**
   * Sends request to change password.
   */
  changePassword() {
    this.errorMessage = '';
    this.successMessage = '';

    if (!this.userId) {
      this.errorMessage = 'User not loaded';
      return;
    }

    if (this.newPassword !== this.confirmPassword) {
      this.errorMessage = 'Passwords do not match';
      return;
    }

    const valid = this.validateNewPassword();
    if (valid) {
      this.errorMessage = valid;
      return;
    }

    this.loading = true;

    this.usersService.changePassword(this.userId, this.oldPassword, this.newPassword)
      .subscribe({
        next: () => {
          this.loading = false;
          this.successMessage = 'Password updated successfully';
          setTimeout(() => this.closeModal(), 1200);
        },
        error: (err) => {
          this.loading = false;
          this.errorMessage = err.error?.message || 'Error updating password';
        }
      });
  }

  closeModal() {
    this.close.emit();
  }
}
