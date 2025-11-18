import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';

import { ProjectsService } from '../../../../services/projects.service';
import { ReportsService } from '../../../../services/reports.service';
import { AuthService } from '../../../../services/auth.service';
import { MeetingsService } from '../../../../services/meetings.service';
import { ProjectDTO } from '../../../../models/project-dto.model';
import { ReportDTO } from '../../../../models/report-dto.model';
import { MeetingDTO } from '../../../../models/meeting-dto.model';

import { forkJoin } from 'rxjs'; 

@Component({
  selector: 'app-employee-dashboard-home',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './employee-dashboard-home.component.html',
  styleUrls: ['./employee-dashboard-home.component.css']
})
export class EmployeeDashboardHomeComponent implements OnInit {

  // Basic user details
  currentUserName: string = 'Employee';
  currentUserId: number | null = null;

  // Statistical counters
  activeProjectsCount = 0;
  submittedReportsCount = 0;
  upcomingMeetingsCount = 0;

  // Lists for UI
  myActiveProjects: ProjectDTO[] = [];
  myRecentReports: any[] = []; // NOTE: mapped reports
  loading = true;

  constructor(
    private projectsService: ProjectsService,
    private reportsService: ReportsService,
    private meetingsService: MeetingsService,
    private auth: AuthService
  ) {}

  ngOnInit(): void {
    this.loadEmployeeData();
  }

  // STEP 1 — Get the logged-in user's info
  loadEmployeeData(): void {
    const saved = localStorage.getItem('user');
    if (!saved) {
      this.loading = false;
      return;
    }

    const email = JSON.parse(saved).email;

    this.auth.getUserByEmail(email).subscribe({
      next: (user: any) => {
        this.currentUserId = user.id;
        this.currentUserName = user.firstName || 'Employee';
        this.loadDashboard();
      },
      error: (err) => {
        console.error('Failed to fetch user details:', err);
        this.loading = false;
      }
    });
  }

  // STEP 2 — Load all dashboard data (parallel)
  loadDashboard(): void {
    if (!this.currentUserId) return;

    forkJoin([
      this.projectsService.getMyProjects(),
      this.reportsService.getByEmployee(this.currentUserId),
      this.meetingsService.getMyMeetings()
    ]).subscribe({
      next: ([projects, reports, meetings]) => {

        const safeReports = reports as ReportDTO[];
        const safeMeetings = meetings as MeetingDTO[];
        const activeProjects = (projects as ProjectDTO[]).filter(
          p => (p.status ?? '').toUpperCase() === 'ACTIVE'
        );

        // ---- STATISTICS ----
        this.activeProjectsCount = activeProjects.length;
        this.submittedReportsCount = safeReports.length;

        this.upcomingMeetingsCount = safeMeetings.filter(
          m => new Date(m.meetingDate!).getTime() > Date.now()
        ).length;

        // ---- LIST DATA ----
        this.myActiveProjects = activeProjects;

        // 👇 THIS IS THE IMPORTANT FIX — MAP SERVER FIELDS
        this.myRecentReports = safeReports
          .map(r => ({
            reportTitle: r.title ?? 'Untitled Report',
            reportStatus: r.status ?? 'OPEN',
            reportDate: r.date ?? null
          }))
          .sort((a, b) => (b.reportDate ?? '').localeCompare(a.reportDate ?? ''))
          .slice(0, 5);

        this.loading = false;
      },
      error: (err) => {
        console.error('Failed to load dashboard data:', err);
        this.loading = false;
      }
    });
  }

  // Reports awaiting review
  getReportsAwaitingReviewCount(): number {
    if (!this.myRecentReports) return 0;

    return this.myRecentReports.filter(
      r => r.reportStatus === 'SUBMITTED' || r.reportStatus === 'IN_REVIEW'
    ).length;
  }
}
