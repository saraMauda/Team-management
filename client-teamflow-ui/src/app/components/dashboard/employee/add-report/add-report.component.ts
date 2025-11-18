import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ReportsService } from '../../../../services/reports.service';
import { ProjectsService } from '../../../../services/projects.service';
import { AuthService } from '../../../../services/auth.service';

@Component({
  selector: 'app-add-report',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './add-report.component.html',
  styleUrls: ['./add-report.component.css']
})
export class AddReportComponent implements OnInit {

  projects: any[] = [];
  selectedProjectId: number | null = null;
  title: string = '';
  description: string = '';
  status: string = 'OPEN';
  hours: number = 8;            // ⬅ הוספתי ברירת מחדל
  successMessage = '';
  errorMessage = '';
  loading = false;

  currentUserId: number | null = null;   // ⬅ חובה לשרת

  constructor(
    private reportsService: ReportsService,
    private projectsService: ProjectsService,
    private auth: AuthService
  ) {}

  ngOnInit(): void {
    this.loadUser();
  }

  loadUser() {
    const stored = localStorage.getItem('user');
    if (!stored) return;

    const email = JSON.parse(stored).email;

    this.auth.getUserByEmail(email).subscribe({
      next: (u) => {
        this.currentUserId = u.id;
        this.loadProjects();
      },
      error: () => this.errorMessage = 'Failed to load user info.'
    });
  }

  loadProjects(): void {
    this.projectsService.getMyProjects().subscribe({
      next: (data) => (this.projects = data),
      error: () => (this.errorMessage = 'Failed to load projects.')
    });
  }

  submitReport(): void {
    if (!this.selectedProjectId || !this.title.trim() || !this.description.trim()) {
      this.errorMessage = 'Please fill out all required fields.';
      return;
    }

    if (!this.currentUserId) {
      this.errorMessage = 'User not found.';
      return;
    }

    const today = new Date().toISOString().split('T')[0]; // yyyy-MM-dd

    const reportData = {
      projectId: this.selectedProjectId,
      userId: this.currentUserId,      // ⬅ חובה
      date: today,                     // ⬅ חובה לשרת
      hours: this.hours,               // ⬅ חובה
      status: this.status,
      description: this.description,  // ⬅ בשרת זה "description" ולא "title"
      title: this.title
    };

    this.loading = true;

    this.reportsService.addReport(reportData).subscribe({
      next: () => {
        this.successMessage = 'Report submitted successfully!';
        this.errorMessage = '';
        this.loading = false;
        this.resetForm();
      },
      error: (err) => {
        console.error('❌ Failed to submit report', err);
        this.errorMessage = 'Failed to submit report.';
        this.successMessage = '';
        this.loading = false;
      }
    });
  }

  resetForm(): void {
    this.selectedProjectId = null;
    this.title = '';
    this.description = '';
    this.status = 'OPEN';
    this.hours = 8;
  }
}
