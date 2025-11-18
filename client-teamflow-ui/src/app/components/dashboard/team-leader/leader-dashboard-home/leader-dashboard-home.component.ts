import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { forkJoin } from 'rxjs';

import { ProjectsService } from '../../../../services/projects.service';
import { ReportsService } from '../../../../services/reports.service';
import { UsersService } from '../../../../services/users.service';
import { TeamService } from '../../../../services/team.service';
import { MeetingsService } from '../../../../services/meetings.service';

// ----------------------------------------------
// --- מודלים דמיוניים (יש להניח שהם קיימים) ---
// ----------------------------------------------
// יש להחליף בייבוא המודלים האמיתיים שלך אם הם קיימים בנתיב אחר
interface ProjectDTO { id?: number; name?: string; status?: string; progress?: number; }
interface ReportDTO { id?: number; employeeId?: number; reportTitle?: string; reportStatus?: string; reportDate?: string; }
interface MeetingDTO { id?: number; meetingDate?: string; }

// ממשק מורחב לדוחות המוצגים בדאשבורד, כולל שם העובד
interface EnrichedReportDTO extends ReportDTO {
  employeeName: string;
}
// ----------------------------------------------


@Component({
  selector: 'app-leader-dashboard-home',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './leader-dashboard-home.component.html',
  styleUrls: ['./leader-dashboard-home.component.css']
})
export class LeaderDashboardHomeComponent implements OnInit {
  
  // Data for KPIs
  currentLeaderName: string = 'Team Leader';
  teamCount = 0;
  activeProjectsCount = 0;
  reportsAwaitingReviewCount = 0;
  upcomingMeetingsCount = 0;

  // Data for lists
  myManagedProjects: ProjectDTO[] = [];
  teamMembers: any[] = []; // List of team members
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

  // Step 1: Load Leader Info
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
        this.currentLeaderName = leader.firstName || 'Team Leader'; 
        this.loadDashboard();
      },
      error: () => {
        this.error = 'Failed to load leader data.';
        this.loading = false;
      }
    });
  }

  // Step 2: Load Dashboard Data (Unified Call)
  loadDashboard(): void {
    if (!this.currentLeaderId) return;

    // 1. Get projects managed by this leader
    const projectsCall = this.projectsService.getByLeader(this.currentLeaderId);
    
    // 2. Get the teams (to find the members)
    const teamsCall = this.teamService.getTeamsByLeader(this.currentLeaderId);
    
    forkJoin({
      projects: projectsCall,
      teams: teamsCall
    }).subscribe({
      next: ({ projects, teams }) => {
        this.myManagedProjects = projects as ProjectDTO[];
        this.activeProjectsCount = this.myManagedProjects.filter(p => (p.status ?? '').toUpperCase() === 'ACTIVE').length;

        const team = teams?.[0];
        this.teamMembers = team?.members ?? []; 
        this.teamCount = this.teamMembers.length;

        const managedProjectIds = this.myManagedProjects.map(p => p.id!).filter(id => id != null) as number[];

        // Continue to load reports and meetings
        this.loadReportsAndMeetings(this.teamMembers, managedProjectIds);
      },
      error: (err) => {
        console.error('Failed to load core data (Projects/Team):', err);
        this.error = 'Failed to load core data.';
        this.loading = false;
      }
    });
  }

  // Step 3: Load Reports and Meetings
  loadReportsAndMeetings(members: any[], projectIds: number[]): void {
    
    // יצירת מפה לשליפה מהירה של שם העובד לפי ה-ID שלו
    const memberNameMap = new Map<number, string>();
    members.forEach(m => memberNameMap.set(m.id, m.name || m.email || `User #${m.id}`));
    
    // A. טעינת כל הדוחות של כל חברי הצוות
    const reportCalls = members.map(m =>
      this.reportsService.getByEmployee(m.id)
    );
    
    // B. טעינת כל הפגישות עבור כל הפרויקטים שהמנהל מנהל
    // משתמשים בשירות getTeamMeetings
    const meetingCalls = projectIds.map(id => this.meetingsService.getTeamMeetings(id));
    
    // איחוד כל הקריאות ל-forkJoin
    const allCalls = [...reportCalls, ...meetingCalls];

    forkJoin(allCalls).subscribe({
      next: (results: any[]) => {
        
        // הפרדת התוצאות
        const reportsResults = results.slice(0, reportCalls.length);
        const meetingsResults = results.slice(reportCalls.length);
        
        // 1. עיבוד דוחות
        const mergedReports: ReportDTO[] = reportsResults.flat().filter(r => r); 
        
        // העשרת הדוחות עם שם העובד והכנתם לתצוגה
        const enrichedReports: EnrichedReportDTO[] = mergedReports.map(report => ({
            ...report,
            employeeName: memberNameMap.get(report.employeeId!) || 'Unknown' 
        }));
        
        // סינון דוחות הדורשים בדיקה
        this.reportsAwaitingReview = enrichedReports.filter(
          (r: EnrichedReportDTO) => r.reportStatus === 'SUBMITTED' || r.reportStatus === 'IN_REVIEW'
        );
        this.reportsAwaitingReviewCount = this.reportsAwaitingReview.length;

        // 2. עיבוד פגישות
        const mergedMeetings: MeetingDTO[] = meetingsResults.flat().filter(m => m);
        
        // ספירת פגישות עתידיות
        this.upcomingMeetingsCount = mergedMeetings.filter(
          (m: MeetingDTO) => new Date(m.meetingDate!).getTime() > Date.now()
        ).length;

        this.loading = false;
      },
      error: (err) => {
        console.error('Error loading reports or meetings:', err);
        this.error = 'Failed to load reports or meetings.';
        this.loading = false;
      }
    });
  }
}