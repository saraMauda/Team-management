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

  leaderId!: number;
  teamReports: any[] = [];

  selectedReport: any = null;
  comments: any[] = [];
  commentsLoading = false;
  newComment = '';
  panelOpen = false;

  // ⭐ שדות חדשים למנגנון עדכון הסטטוס ⭐
  updatedStatus: string = '';
  savingStatus = false;

  constructor(
    private reportsService: ReportsService,
    private usersService: UsersService,
    private auth: AuthService,
    private http: HttpClient
  ) {}

  ngOnInit() {
    this.loadLeaderId();
  }

  loadLeaderId() {
    const email = this.auth.getCurrentUserEmail();
    if (!email) return;

    this.usersService.getByEmail(email).subscribe(user => {
      this.leaderId = user.id;
      this.loadReports();
    });
  }

  loadReports() {
    this.http.get<any[]>(`${API_BASE_URL}/reports/byLeader/${this.leaderId}`, {
      withCredentials: true
    }).subscribe({
      next: reps => {
        this.teamReports = reps.sort(
          (a, b) => new Date(b.reportDate).getTime() - new Date(a.reportDate).getTime()
        );
      }
    });
  }

  openPanel(report: any) {
    this.selectedReport = report;
    this.updatedStatus = report.status; // ⭐ סטטוס התחלתי ⭐
    this.panelOpen = true;
    this.commentsLoading = true;

    this.http.get<any[]>(`${API_BASE_URL}/report-comments/${report.id}`, {
      withCredentials: true
    }).subscribe({
      next: comments => {
        this.comments = comments;
        this.commentsLoading = false;
      },
      error: () => this.commentsLoading = false
    });
  }

  closePanel() {
    this.panelOpen = false;
    this.selectedReport = null;
    this.comments = [];
    this.newComment = '';
  }

  submitComment() {
    if (!this.newComment.trim()) return;

    const body = {
      text: this.newComment,
      userId: this.leaderId
    };

    this.http.post(
      `${API_BASE_URL}/report-comments/add/${this.selectedReport.id}`,
      body,
      { withCredentials: true }
    ).subscribe({
      next: (createdComment: any) => {
        this.comments.push(createdComment);
        this.newComment = '';
      }
    });
  }

  // ⭐ פונקציה לעדכון סטטוס ⭐
  updateStatus() {
    if (!this.selectedReport) return;

    this.savingStatus = true;

    const body = { status: this.updatedStatus };

this.reportsService.updateStatus(this.selectedReport.id, this.updatedStatus)
  .subscribe({
    next: (updated) => {
      this.selectedReport.status = updated.status;

      this.teamReports = this.teamReports.map(r =>
        r.id === updated.id ? updated : r
      );

      this.savingStatus = false;
    },
    error: () => {
      alert('Failed to update status');
      this.savingStatus = false;
    }
  });
  }

  getStatusColor(status: string) {
    switch (status) {
      case 'APPROVED': return '#4caf50';
      case 'REJECTED': return '#f44336';
      case 'IN_REVIEW': return '#ff9800';
      default: return '#d4dbe4';
    }
  }
}
