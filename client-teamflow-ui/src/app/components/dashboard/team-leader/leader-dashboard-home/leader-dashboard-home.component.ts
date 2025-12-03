import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { forkJoin } from 'rxjs';

import { ProjectsService } from '../../../../services/projects.service';
import { ReportsService } from '../../../../services/reports.service';
import { UsersService } from '../../../../services/users.service';
import { TeamService } from '../../../../services/team.service';
import { MeetingsService } from '../../../../services/meetings.service';

import { ProjectDTO } from '../../../../models/project-dto.model';
import { ReportDTO } from '../../../../models/report-dto.model';
import { MeetingDTO } from '../../../../models/meeting-dto.model';

interface EnrichedReportDTO extends ReportDTO {
  employeeName: string;
}

@Component({
  selector: 'app-leader-dashboard-home',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './leader-dashboard-home.component.html',
  styleUrls: ['./leader-dashboard-home.component.css']
})
export class LeaderDashboardHomeComponent implements OnInit {

  currentLeaderName: string = 'Team Leader';
  teamCount = 0;
  activeProjectsCount = 0;
  reportsAwaitingReviewCount = 0;
  upcomingMeetingsCount = 0;

  myManagedProjects: ProjectDTO[] = [];
  teamMembers: any[] = [];
  reportsAwaitingReview: EnrichedReportDTO[] = [];

  loading = true;
  error = '';
  currentLeaderId: number | null = null;

  constructor(
    private usersService: UsersService,
    private teamService: TeamService,
    private reportsService: ReportsService,
    private projectsService: ProjectsService,
    private meetingsService: MeetingsService
  ) {}

  ngOnInit(): void {
    this.loadLeaderInfo();
  }

  normalizeStatus(status: string | null | undefined): string {
    if (!status) return 'IN_REVIEW';
    status = status.toUpperCase();
    if (status === 'SUBMITTED' || status === 'OPEN') {
      return 'IN_REVIEW';
    }
    return status;
  }

  loadLeaderInfo(): void {
    const stored = localStorage.getItem('user');
    if (!stored) {
      this.error = 'User not logged in.';
      this.loading = false;
      return;
    }

    const email = JSON.parse(stored).email;

    this.usersService.getByEmail(email).subscribe({
      next: (leader: any) => {
        this.currentLeaderId = leader.id;
        this.currentLeaderName = leader.name || 'Team Leader';
        this.loadDashboard();
      },
      error: () => {
        this.error = 'Failed to load leader data.';
        this.loading = false;
      }
    });
  }

  loadDashboard(): void {
    if (!this.currentLeaderId) return;

    const projectsCall = this.projectsService.getByLeader(this.currentLeaderId);
    const teamsCall = this.teamService.getTeamsByLeader(this.currentLeaderId);

    forkJoin({
      projects: projectsCall,
      teams: teamsCall
    }).subscribe({
      next: ({ projects, teams }) => {

        this.myManagedProjects = projects;
        this.activeProjectsCount = this.myManagedProjects
          .filter(p => (p.status || '').toUpperCase() === 'ACTIVE')
          .length;

        const team = teams?.[0];
        this.teamMembers = team?.members ?? [];
        this.teamCount = this.teamMembers.length;

        const projectIds = this.myManagedProjects
          .map(p => p.id!)
          .filter(id => id != null);

        this.loadReportsAndMeetings(this.teamMembers, projectIds);
      },
      error: () => {
        this.error = 'Failed to load core data.';
        this.loading = false;
      }
    });
  }

  loadReportsAndMeetings(members: any[], projectIds: number[]): void {

    const memberNameMap = new Map<number, string>();
    members.forEach(m => memberNameMap.set(m.id, m.name));

    const nameOf = (id: number): string =>
      memberNameMap.get(id) || 'Unknown';

    const meetingCalls = projectIds.map(id =>
      this.meetingsService.getTeamMeetings(id)
    );

    forkJoin([
      this.reportsService.getAll(),
      ...meetingCalls
    ]).subscribe({
      next: (results: any[]) => {

        const allReports = results[0];
        const meetingsArrays = results.slice(1);

        const allMeetings: MeetingDTO[] = meetingsArrays.flat();

const relevantReports = allReports.filter((r: ReportDTO) =>
  members.some((m: any) => m.id === r.userId)
);

const enrichedReports: EnrichedReportDTO[] = relevantReports.map((report: ReportDTO) => ({
  ...report,
  status: this.normalizeStatus(report.status),
  employeeName: nameOf(report.userId!)
}));


        this.reportsAwaitingReview = enrichedReports.filter(
          r => r.status === 'IN_REVIEW'
        );

        this.reportsAwaitingReviewCount = this.reportsAwaitingReview.length;

this.upcomingMeetingsCount = allMeetings.filter(m => {
  if (!m.meetingDate) return false;

  const cleaned = m.meetingDate.trim();
  const [year, month, day] = cleaned.split("-").map(n => Number(n));

  if (!year || !month || !day) return false;

  const date = new Date(year, month - 1, day);

  return date.getTime() > Date.now();
}).length;

this.loading = false;



      },
      error: () => {
        this.error = 'Failed to load reports or meetings.';
        this.loading = false;
      }
    });
  }
}
