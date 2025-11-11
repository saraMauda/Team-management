import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReportsService } from '../../../../services/reports.service';

@Component({
  selector: 'app-team-reports',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './team-reports.component.html',
  styleUrls: ['./team-reports.component.css']
})
export class TeamReportsComponent implements OnInit {
  reports: any[] = [];
  loading = true;
  error: string | null = null;

  constructor(private reportsService: ReportsService) {}

  ngOnInit(): void {
    this.loadReports();
  }

  loadReports(): void {
    this.reportsService.getAll().subscribe({
      next: (data) => {
        // 🔹 כאן תוכלי לסנן רק את הדוחות של העובדים בצוות שלך (אם יש שדה כזה)
        this.reports = data.filter((r: any) => r.status !== 'ARCHIVED');
        this.loading = false;
      },
      error: (err) => {
        console.error('❌ Failed to load reports', err);
        this.error = 'Failed to load team reports.';
        this.loading = false;
      }
    });
  }

  getStatusColor(status: string): string {
    switch (status) {
      case 'OPEN': return '#ff9800';
      case 'IN_REVIEW': return '#03a9f4';
      case 'APPROVED': return '#4caf50';
      case 'REJECTED': return '#f44336';
      default: return '#cfd9e4';
    }
  }
}
