import { Component, OnInit } from '@angular/core';
import { CommonModule, DatePipe } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ReportsService } from '../../../../services/reports.service';
import { UsersService } from '../../../../services/users.service';
import { AuthService } from '../../../../services/auth.service';
import { API_BASE_URL } from '../../../../app.config';
import { HttpClient } from '@angular/common/http';

@Component({
  selector: 'app-team-reports',
  standalone: true,
  imports: [CommonModule, FormsModule, DatePipe],
  templateUrl: './team-reports.component.html',
  styleUrls: ['./team-reports.component.css']
})
export class TeamReportsComponent implements OnInit {

  leaderId: number = 0;
  teamReports: any[] = [];

  selectedReport: any = null;
  comments: any[] = [];
  newComment: string = '';
  panelOpen: boolean = false;

  constructor(
    private reportsService: ReportsService,
    private usersService: UsersService,
    private auth: AuthService,
    private http: HttpClient
  ) {}

  ngOnInit(): void {
    this.loadLeaderId();
  }

  /** ✨ שליפת ה־leaderId מהעוגייה באמצעות אימייל */
  loadLeaderId() {
    const email = this.auth.getCurrentUserEmail();
    if (!email) {
      console.error('❌ No email in cookie');
      return;
    }

    this.usersService.getByEmail(email).subscribe({
      next: user => {
        this.leaderId = user.id;
        this.loadReports();
      },
      error: err => console.error(err)
    });
  }

  /** 🔥 שליפת כל הדוחות של הצוות */
  loadReports() {
    this.http.get<any[]>(`${API_BASE_URL}/reports/byLeader/${this.leaderId}`, {
      withCredentials: true
    }).subscribe({
      next: reps => {
        this.teamReports = reps;
      },
      error: err => console.error(err)
    });
  }

  /** 🔥 פתיחת חלון תגובות + טעינת תגובות */
  openPanel(report: any) {
    this.selectedReport = report;
    this.panelOpen = true;

    this.http.get<any[]>(`${API_BASE_URL}/report-comments/${report.id}`, {
      withCredentials: true
    }).subscribe({
      next: comments => this.comments = comments,
      error: err => console.error(err)
    });
  }

  closePanel() {
    this.panelOpen = false;
    this.selectedReport = null;
    this.newComment = '';
    this.comments = [];
  }

  /** ✨ הוספת תגובה */
  submitComment() {
    if (!this.newComment.trim()) return;

    const body = { text: this.newComment };

    this.http.post(`${API_BASE_URL}/report-comments/add/${this.selectedReport.id}`,
      body,
      { withCredentials: true }
    ).subscribe({
      next: () => {
        this.newComment = '';
        this.openPanel(this.selectedReport); // Reload panel
      },
      error: err => console.error('❌ Error adding comment:', err)
    });
  }
}
