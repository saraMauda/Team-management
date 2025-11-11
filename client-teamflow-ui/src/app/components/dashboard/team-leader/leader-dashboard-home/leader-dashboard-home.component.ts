import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
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

  constructor(
    private usersService: UsersService,
    private projectsService: ProjectsService,
    private reportsService: ReportsService,
    private meetingsService: MeetingsService
  ) {}

  ngOnInit(): void {
    Promise.all([
      this.usersService.getAll().toPromise(),
      this.projectsService.getAll().toPromise(),
      this.reportsService.getAll().toPromise(),
      this.meetingsService.getAll().toPromise()
    ])
      .then(([users, projects, reports, meetings]) => {
        const safeUsers = users ?? [];
        const safeProjects = projects ?? [];
        const safeReports = reports ?? [];
        const safeMeetings = meetings ?? [];

        this.teamCount = safeUsers.filter(u => (u as any).role === 'ROLE_EMPLOYEE').length;
        this.activeProjects = safeProjects.filter(p => (p as any).status === 'ACTIVE').length;
        this.openReports = safeReports.filter(r => (r as any).status === 'OPEN').length;
        this.upcomingMeetings = safeMeetings.filter(m => {
          const dateValue = (m as any).date;
          return dateValue && new Date(dateValue) > new Date();
        }).length;
      })
      .finally(() => (this.loading = false));
  }
}
