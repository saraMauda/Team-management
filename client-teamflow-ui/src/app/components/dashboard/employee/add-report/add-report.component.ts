import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ReportsService } from '../../../../services/reports.service';
import { ProjectsService } from '../../../../services/projects.service';

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
  successMessage = '';
  errorMessage = '';
  loading = false;

  constructor(
    private reportsService: ReportsService,
    private projectsService: ProjectsService
  ) {}

  ngOnInit(): void {
    this.loadProjects();
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

    const reportData = {
      projectId: this.selectedProjectId,
      title: this.title,
      description: this.description,
      status: this.status
    };

    this.loading = true;
    this.reportsService.create(reportData).subscribe({
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
  }
}
