import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ProjectsService } from '../../../../services/projects.service';
import { ReportsService } from '../../../../services/reports.service';
import { MeetingsService } from '../../../../services/meetings.service';

@Component({
  selector: 'app-employee-dashboard-home',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './employee-dashboard-home.component.html',
  styleUrls: ['./employee-dashboard-home.component.css']
})
export class EmployeeDashboardHomeComponent implements OnInit {
  activeProjects = 0;
  submittedReports = 0;
  upcomingMeetings = 0;
  tasksInProgress = 0;
  loading = true;

  constructor(
    private projectsService: ProjectsService,
    private reportsService: ReportsService,
    private meetingsService: MeetingsService
  ) {}

  ngOnInit(): void {
    Promise.all([
      this.projectsService.getAll().toPromise(),
      this.reportsService.getAll().toPromise(),
      this.meetingsService.getAll().toPromise()
    ])
      .then(([projects, reports, meetings]) => {
        const safeProjects = projects ?? [];
        const safeReports = reports ?? [];
        const safeMeetings = meetings ?? [];

        this.activeProjects = safeProjects.filter(
          (p: any) => p.status === 'ACTIVE'
        ).length;

        this.submittedReports = safeReports.filter(
          (r: any) => r.status === 'SUBMITTED' || r.status === 'OPEN'
        ).length;

        this.upcomingMeetings = safeMeetings.filter(
          (m: any) => new Date(m.date) > new Date()
        ).length;

        this.tasksInProgress = safeProjects.filter(
          (p: any) => p.progress < 100
        ).length;
      })
      .finally(() => (this.loading = false));
  }
}
