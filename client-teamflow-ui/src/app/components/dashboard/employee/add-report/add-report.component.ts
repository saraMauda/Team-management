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
  status: string = 'IN_REVIEW';
  hours: number = 8;

  loading = false;

  currentUserId: number | null = null;

  // TOAST
  toastVisible = false;
  toastMessage = '';
  toastType: 'success' | 'error' = 'success';

  constructor(
    private reportsService: ReportsService,
    private projectsService: ProjectsService,
    private auth: AuthService
  ) {}

  ngOnInit(): void {
    this.loadUser();
  }

  showToast(msg: string, type: 'success' | 'error' = 'success') {
    this.toastMessage = msg;
    this.toastType = type;
    this.toastVisible = true;

    setTimeout(() => this.toastVisible = false, 5000);
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
      error: () => this.showToast('Failed to load user info.', 'error')
    });
  }

  loadProjects(): void {
    this.projectsService.getMyProjects().subscribe({
      next: (data) => (this.projects = data),
      error: () => this.showToast('Failed to load projects.', 'error')
    });
  }

  submitReport(): void {
    if (!this.selectedProjectId || !this.title.trim() || !this.description.trim()) {
      this.showToast('Please fill out all required fields.', 'error');
      return;
    }

    if (!this.currentUserId) {
      this.showToast('User not found.', 'error');
      return;
    }

    const today = new Date().toISOString().split('T')[0];

    const reportData = {
      projectId: this.selectedProjectId,
      userId: this.currentUserId,
      date: today,
      hours: this.hours,
      status: this.status,
      description: this.description,
      title: this.title
    };

    this.loading = true;

    this.reportsService.addReport(reportData).subscribe({
      next: () => {
        this.showToast('Report submitted successfully!', 'success');
        this.loading = false;
        this.resetForm();
      },
      error: () => {
        this.showToast('Failed to submit report.', 'error');
        this.loading = false;
      }
    });
  }

  resetForm(): void {
    this.selectedProjectId = null;
    this.title = '';
    this.description = '';
    this.status = 'IN_REVIEW';
    this.hours = 8;
  }
}
