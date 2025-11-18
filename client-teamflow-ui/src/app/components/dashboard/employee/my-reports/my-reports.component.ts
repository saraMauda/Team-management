import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReportsService } from '../../../../services/reports.service';
import { AuthService } from '../../../../services/auth.service';

@Component({
  selector: 'app-my-reports',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './my-reports.component.html',
  styleUrls: ['./my-reports.component.css']
})
export class MyReportsComponent implements OnInit {
  reports: any[] = [];
  loading = true;
  error: string | null = null;
  currentUserId: number | null = null;

  constructor(
    private reportsService: ReportsService,
    private auth: AuthService
  ) {}

  ngOnInit(): void {
    this.loadUserInfo();
  }

  loadUserInfo() {
    const stored = localStorage.getItem('user');
    if (!stored) {
      this.error = 'User not logged in.';
      this.loading = false;
      return;
    }

    const email = JSON.parse(stored).email;

    this.auth.getUserByEmail(email).subscribe({
      next: (user) => {
        this.currentUserId = user.id;
        this.loadReports();
      },
      error: () => {
        this.error = 'Failed to load user info.';
        this.loading = false;
      }
    });
  }

  loadReports(): void {
    if (!this.currentUserId) return;

    this.reportsService.getByEmployee(this.currentUserId).subscribe({
      next: (data: any) => {
        this.reports = data.sort(
          (a: any, b: any) => new Date(b.date).getTime() - new Date(a.date).getTime()
        );
        this.loading = false;
      },
      error: (err: any) => {
        console.error('❌ Failed to load reports', err);
        this.error = 'Failed to load your reports.';
        this.loading = false;
      }
    });
  }

  getStatusColor(status: string): string {
    switch (status) {
      case 'APPROVED':
        return '#4caf50';
      case 'REJECTED':
        return '#f44336';
      case 'IN_REVIEW':
        return '#ff9800';
      default:
        return '#cfd9e4';
    }
  }
}
