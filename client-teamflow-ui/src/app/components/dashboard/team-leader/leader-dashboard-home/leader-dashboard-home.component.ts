import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { forkJoin } from 'rxjs';
import { ProjectsService } from '../../../../services/projects.service';
import { ReportsService } from '../../../../services/reports.service';
import { UsersService } from '../../../../services/users.service';
import { TeamService } from '../../../../services/team.service';

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
  loading = true;
  error = '';

  currentLeaderId: number | null = null;

  constructor(
    private usersService: UsersService,
    private teamService: TeamService,
    private reportsService: ReportsService,
    private projectsService: ProjectsService
  ) {}

  ngOnInit(): void {
    this.loadLeaderInfo();
  }

  // שלב 1: טעינת פרטי המנהל לפי עוגייה
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
        this.loadTeamData();
      },
      error: () => {
        this.error = 'Failed to load leader data.';
        this.loading = false;
      }
    });
  }

  // שלב 2: טעינת הצוות של ראש הצוות
  loadTeamData(): void {
    if (!this.currentLeaderId) return;

    this.teamService.getTeamsByLeader(this.currentLeaderId).subscribe({
      next: (teams: any[]) => {
        const team = teams?.[0];
        const members = team?.members ?? [];

        this.teamCount = members.length;

        if (members.length === 0) {
          // אין עובדים בצוות → אין דוחות ואין פרויקטים
          this.activeProjects = 0;
          this.openReports = 0;
          this.loading = false;
          return;
        }

        this.loadReportsForTeam(members);
      },
      error: () => {
        this.error = 'Failed to load team.';
        this.loading = false;
      }
    });
  }

  // שלב 3: טעינת דוחות של כל העובדים בצוות
  loadReportsForTeam(members: any[]): void {
    const reportCalls = members.map(m =>
      this.reportsService.getByEmployee(m.id)
    );

    forkJoin(reportCalls).subscribe({
      next: (allReports: any[]) => {
        // מאחדים לדוחות יחידים
        const mergedReports = allReports.flat();
        this.openReports = mergedReports.filter(r => r.status === 'OPEN').length;

        this.loadProjectsForDashboard();
      },
      error: () => {
        this.error = 'Failed to load reports.';
        this.loading = false;
      }
    });
  }

  // שלב 4: טעינת כל הפרויקטים (מותר ל־Leader לפי השרת)
  loadProjectsForDashboard(): void {
    this.projectsService.getAll().subscribe({
      next: (projects: any[]) => {
        this.activeProjects = projects.filter(p => p.status === 'ACTIVE').length;
        this.loading = false;
      },
      error: () => {
        this.error = 'Failed to load projects.';
        this.loading = false;
      }
    });
  }
}
