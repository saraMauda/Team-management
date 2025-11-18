import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ProjectsService } from '../../../../services/projects.service';
import { ReportsService } from '../../../../services/reports.service';
import { AuthService } from '../../../../services/auth.service';

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
  upcomingMeetings = 0; // אין meetings בשרת → תמיד 0
  tasksInProgress = 0;

  loading = true;
  currentUserId: number | null = null;

  constructor(
    private projectsService: ProjectsService,
    private reportsService: ReportsService,
    private auth: AuthService
  ) {}

  ngOnInit(): void {
    this.loadEmployeeData();
  }

  // שלב ראשון — להביא את המשתמש מהעוגייה
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
        this.loadDashboard();
      },
      error: () => {
        this.loading = false;
      }
    });
  }

  // שלב שני — נטען דוחות ופרויקטים רק של העובד
  loadDashboard(): void {
    if (!this.currentUserId) return;

    Promise.all([
      this.projectsService.getAll().toPromise(),
      this.reportsService.getByEmployee(this.currentUserId).toPromise()
    ])
      .then(([projects, reports]) => {

        const safeProjects = projects ?? [];
        const safeReports = reports ?? [];

        // כמה פרויקטים פעילים יש
        this.activeProjects = safeProjects.filter(
          (p: any) => p.status === 'ACTIVE'
        ).length;

        // כמה דוחות העובד שלח
        this.submittedReports = safeReports.length;

        // כמה משימות בתהליך
        this.tasksInProgress = safeProjects.filter(
          (p: any) => p.progress < 100
        ).length;

        // meetings לא קיימים → נשאר רק 0
        this.upcomingMeetings = 0;

      })
      .finally(() => {
        this.loading = false;
      });
  }
}
