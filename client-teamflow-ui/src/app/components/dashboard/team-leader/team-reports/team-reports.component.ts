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

  updatedStatus: string = '';
  savingStatus = false;

  // ---------- TOAST ----------
  toastVisible = false;
  toastMessage = '';
  toastType: 'success' | 'error' = 'success';

  showToast(msg: string, type: 'success' | 'error' = 'success') {
    this.toastMessage = msg;
    this.toastType = type;
    this.toastVisible = true;

    setTimeout(() => (this.toastVisible = false), 5000);
  }

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
      },
      error: () => this.showToast('Failed to load reports', 'error')
    });
  }

  openPanel(report: any) {
    this.selectedReport = report;
    this.updatedStatus = report.status;
    this.panelOpen = true;
    this.commentsLoading = true;

    this.http.get<any[]>(`${API_BASE_URL}/report-comments/${report.id}`, {
      withCredentials: true
    }).subscribe({
      next: comments => {
        this.comments = comments;
        this.commentsLoading = false;
      },
      error: () => {
        this.commentsLoading = false;
        this.showToast('Failed to load comments', 'error');
      }
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

    const body = { text: this.newComment, userId: this.leaderId };

    this.http.post(
      `${API_BASE_URL}/report-comments/add/${this.selectedReport.id}`,
      body,
      { withCredentials: true }
    ).subscribe({
      next: (createdComment: any) => {
        this.comments.push(createdComment);
        this.newComment = '';
        this.showToast('Comment added', 'success');
      },
      error: () => this.showToast('Failed to add comment', 'error')
    });
  }

  updateStatus() {
    if (!this.selectedReport) return;

    this.savingStatus = true;

    this.reportsService.updateStatus(this.selectedReport.id, this.updatedStatus)
      .subscribe({
        next: updated => {
          this.selectedReport.status = updated.status;
          this.teamReports = this.teamReports.map(r =>
            r.id === updated.id ? updated : r
          );

          this.savingStatus = false;
          this.showToast('Status updated successfully', 'success');
        },
        error: () => {
          this.savingStatus = false;
          this.showToast('Failed to update status', 'error');
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
