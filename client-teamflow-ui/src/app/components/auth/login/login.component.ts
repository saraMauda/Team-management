import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AuthService } from '../../../services/auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent {
  credentials = { email: '', password: '' };
  errorMessage = '';
  loading = false;

  constructor(private authService: AuthService) {}

  onSubmit() {
    this.loading = true;
    this.errorMessage = '';

    this.authService.signin(this.credentials.email, this.credentials.password)
      .subscribe({
        next: () => {
          console.log('✅ Logged in successfully');
          this.loading = false;
        },
        error: (err: any) => {
          console.error('❌ Login failed', err);
          this.loading = false;
          this.errorMessage = 'Invalid email or password';
        }
      });
  }
}
