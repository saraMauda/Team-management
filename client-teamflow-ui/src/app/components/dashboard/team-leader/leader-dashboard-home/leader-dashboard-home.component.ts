import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { forkJoin } from 'rxjs';
import { ProjectsService } from '../../../../services/projects.service';
import { ReportsService } from '../../../../services/reports.service';
import { UsersService } from '../../../../services/users.service';
import { MeetingsService } from '../../../../services/meetings.service';

@Component({
  selector: 'app-leader-dashboard-home',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './leader-dashboard-home.component.html',
  styleUrls: ['./leader-dashboard-home.component.css']
})
export class LeaderDashboardHomeComponent implements OnInit {
  teamCount = 0;
  activeProjects = 0;
  openReports = 0;
  upcomingMeetings = 0;
  loading = true;
  error = '';

  constructor(
    private usersService: UsersService,
    private projectsService: ProjectsService,
    private reportsService: ReportsService,
    private meetingsService: MeetingsService
  ) {}

  ngOnInit(): void {
    this.loadDashboardData();
  }

  loadDashboardData(): void {
    forkJoin({
      users: this.usersService.getAll(),
      projects: this.projectsService.getAll(),
      reports: this.reportsService.getAll(),
      meetings: this.meetingsService.getAll()
    }).subscribe({
      next: ({ users, projects, reports, meetings }) => {
        const safeUsers = users ?? [];
        const safeProjects = projects ?? [];
        const safeReports = reports ?? [];
        const safeMeetings = meetings ?? [];

        this.teamCount = safeUsers.filter(
          (u: any) => u.role === 'ROLE_EMPLOYEE'
        ).length;

        this.activeProjects = safeProjects.filter(
          (p: any) => p.status === 'ACTIVE'
        ).length;

        this.openReports = safeReports.filter(
          (r: any) => r.status === 'OPEN'
        ).length;

        this.upcomingMeetings = safeMeetings.filter((m: any) => {
          const dateValue = m.date;
          return dateValue && new Date(dateValue) > new Date();
        }).length;
      },
      error: (err) => {
        console.error('❌ Failed to load dashboard data', err);
        this.error = 'Failed to load data from server.';
      },
      complete: () => {
        this.loading = false;
      }
    });
  }
}
