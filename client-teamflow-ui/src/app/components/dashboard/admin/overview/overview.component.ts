import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';

import { UsersService } from '../../../../services/users.service';
import { ProjectsService } from '../../../../services/projects.service';
import { ReportsService } from '../../../../services/reports.service';
import { MeetingsService } from '../../../../services/meetings.service';

import { UsersDTO } from '../../../../models/users-dto.model';
import { ProjectDTO } from '../../../../models/project-dto.model';
import { ReportDTO } from '../../../../models/report-dto.model';
import { MeetingDTO } from '../../../../models/meeting-dto.model';

@Component({
  selector: 'app-overview',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './overview.component.html',
  styleUrls: ['./overview.component.css']
})
export class OverviewComponent implements OnInit {

  loading = false;
  error: string | null = null;

  totalUsers = 0;
  activeUsers = 0;

  totalProjects = 0;
  activeProjects = 0;

  totalReports = 0;
  totalMeetings = 0;

  recentProjects: ProjectDTO[] = [];
  recentReports: ReportDTO[] = [];
  recentMeetings: MeetingDTO[] = [];

  constructor(
    private usersService: UsersService,
    private projectsService: ProjectsService,
    private reportsService: ReportsService,
    private meetingsService: MeetingsService
  ) {}

  ngOnInit(): void {
    this.loadData();
  }

  private loadData(): void {
    this.loading = true;
    this.error = null;

    let pending = 4;
    const done = () => {
      pending--;
      if (pending === 0) {
        this.loading = false;
      }
    };

    // Users
    this.usersService.getAll().subscribe({
      next: (users: UsersDTO[]) => {
        this.totalUsers = users.length;
        this.activeUsers = users.filter(u => u.active).length;
        done();
      },
      error: err => {
        console.error('Error loading users', err);
        this.error = this.error ?? 'Failed to load some data from server.';
        done();
      }
    });

    // Projects
    this.projectsService.getAll().subscribe({
      next: (projects: ProjectDTO[]) => {
        this.totalProjects = projects.length;
        this.activeProjects = projects.filter(
          p => (p.projectStatus ?? '').toLowerCase() === 'active'
        ).length;

        // recent projects (נניח לפי תאריך התחלה / עדכון – אם אין, ניקח פשוט ראשונים)
        this.recentProjects = [...projects]
          .sort((a, b) => (b.lastUpdated ?? '').localeCompare(a.lastUpdated ?? ''))
          .slice(0, 5);

        done();
      },
      error: err => {
        console.error('Error loading projects', err);
        this.error = this.error ?? 'Failed to load some data from server.';
        done();
      }
    });

    // Reports
    this.reportsService.getAll().subscribe({
      next: (reports: ReportDTO[]) => {
        this.totalReports = reports.length;

        this.recentReports = [...reports]
          .sort((a, b) => (b.reportDate ?? '').localeCompare(a.reportDate ?? ''))
          .slice(0, 5);

        done();
      },
      error: err => {
        console.error('Error loading reports', err);
        this.error = this.error ?? 'Failed to load some data from server.';
        done();
      }
    });

    // Meetings
    this.meetingsService.getAll().subscribe({
      next: (meetings: MeetingDTO[]) => {
        this.totalMeetings = meetings.length;

        this.recentMeetings = [...meetings]
          .sort((a, b) => (b.meetingDate ?? '').localeCompare(a.meetingDate ?? ''))
          .slice(0, 5);

        done();
      },
      error: err => {
        console.error('Error loading meetings', err);
        this.error = this.error ?? 'Failed to load some data from server.';
        done();
      }
    });
  }

  // פונקציה קטנה לבר גרפי "פשוט" לאקטיביות
  getActiveUsersPercent(): number {
    if (!this.totalUsers) return 0;
    return Math.round((this.activeUsers / this.totalUsers) * 100);
  }

  getActiveProjectsPercent(): number {
    if (!this.totalProjects) return 0;
    return Math.round((this.activeProjects / this.totalProjects) * 100);
  }
}
