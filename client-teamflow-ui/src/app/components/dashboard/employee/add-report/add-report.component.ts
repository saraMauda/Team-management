import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, NgForm } from '@angular/forms';
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
  status: 'IN_REVIEW' | 'APPROVED' | 'REJECTED' = 'IN_REVIEW';
  hours: number | null = null;

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

  /* ===================== TOAST ===================== */
  showToast(msg: string, type: 'success' | 'error' = 'success') {
    this.toastMessage = msg;
    this.toastType = type;
    this.toastVisible = true;
    setTimeout(() => (this.toastVisible = false), 5000);
  }

  /* ===================== LOAD USER & PROJECTS ===================== */
  private loadUser(): void {
    const stored = localStorage.getItem('user');
    if (!stored) {
      this.showToast('User not found in local storage.', 'error');
      return;
    }

    const email = JSON.parse(stored).email;

    this.auth.getUserByEmail(email).subscribe({
      next: (u) => {
        this.currentUserId = u.id;
        this.loadProjects();
      },
      error: () => this.showToast('Failed to load user info.', 'error')
    });
  }

  private loadProjects(): void {
    this.projectsService.getMyProjects().subscribe({
      next: (data) => (this.projects = data || []),
      error: () => this.showToast('Failed to load projects.', 'error')
    });
  }

  /* ===================== EXTRA VALIDATION ===================== */
  isHoursRangeValid(): boolean {
    return this.hours !== null && this.hours >= 1 && this.hours <= 12;
  }

  /* ===================== SUBMIT REPORT ===================== */
  submitReport(form: NgForm): void {
    // בדיקות אנגולר (required, minlength, וכו')
    if (form.invalid || !this.isHoursRangeValid()) {
      form.control.markAllAsTouched();
      this.showToast('Please fix the errors in the form.', 'error');
      return;
    }

    if (this.currentUserId == null || this.selectedProjectId == null) {
      this.showToast('User or project not found.', 'error');
      return;
    }

    const today = new Date().toISOString().split('T')[0];

    const reportData = {
      projectId: this.selectedProjectId,
      userId: this.currentUserId,
      date: today,
      hours: this.hours!,             
      status: this.status,
      description: this.description.trim(),
      title: this.title.trim()
    };

    this.loading = true;

    this.reportsService.addReport(reportData).subscribe({
      next: () => {
        this.showToast('Report submitted successfully!', 'success');
        this.loading = false;
        this.resetForm(form);
      },
      error: () => {
        this.showToast('Failed to submit report.', 'error');
        this.loading = false;
      }
    });
  }

  private resetForm(form: NgForm): void {
    form.resetForm(); // מאפס גם את ה־ngForm והמצב של touched / submitted
    this.status = 'IN_REVIEW';
    this.hours = null;
  }
}
