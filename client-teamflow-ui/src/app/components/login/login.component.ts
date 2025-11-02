import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { HttpClient, HttpClientModule } from '@angular/common/http';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule, HttpClientModule],
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent {
  email: string = '';
  password: string = '';

  constructor(private http: HttpClient) {}

  onLogin() {
    const user = { email: this.email, password: this.password };

    this.http.post('http://localhost:8080/api/login', user).subscribe({
      next: (res) => {
        console.log('Login successful:', res);
        alert('Welcome to TeamFlow!');
      },
      error: (err) => {
        console.error('Login failed:', err);
        alert('Invalid credentials!');
      }
    });
  }
}
