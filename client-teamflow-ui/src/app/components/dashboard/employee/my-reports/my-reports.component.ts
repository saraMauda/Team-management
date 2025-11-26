import { Component, OnInit } from '@angular/core';
import { CommonModule, DatePipe } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ReportsService } from '../../../../services/reports.service';
import { AuthService } from '../../../../services/auth.service';
import { HttpClient } from '@angular/common/http';
import { forkJoin, of } from 'rxjs';
import { switchMap, map } from 'rxjs/operators';

@Component({
  selector: 'app-my-reports',
  standalone: true,
  imports: [CommonModule, FormsModule, DatePipe],
  templateUrl: './my-reports.component.html',
  styleUrls: ['./my-reports.component.css']
})
export class MyReportsComponent implements OnInit {

  reports: any[] = [];
  loading = true;
  error: string | null = null;
  currentUserId: number | null = null;

  selectedReport: any = null;
  comments: any[] = [];
  newComment: string = '';
  commentsLoading = false;
  panelOpen: boolean = false;

  constructor(
    private reportsService: ReportsService,
    private auth: AuthService,
    private http: HttpClient
  ) {}

  ngOnInit(): void {
    this.loadUserInfo();
  }

  // ⭐ Normalize report status (DB → UI)
  normalizeStatus(s: string | null | undefined): string {
    if (!s) return 'IN_REVIEW';

    s = s.toUpperCase();

    if (s === 'SUBMITTED' || s === 'OPEN') return 'IN_REVIEW';

    return s;
  }

  loadUserInfo() {
    const email = this.auth.getCurrentUserEmail();
    if (!email) {
      this.error = 'User not logged in.';
      return;
    }

    this.auth.getUserByEmail(email).subscribe({
      next: (user) => {
        this.currentUserId = user.id;
        this.loadReports();
      },
      error: () => this.error = 'Failed to load user info.'
    });
  }

  loadReports(): void {
    if (!this.currentUserId) return;

    this.loading = true;

    this.reportsService.getByEmployee(this.currentUserId).pipe(
      switchMap((reports: any[]) => {
        if (reports.length === 0) return of([]);

        const calls = reports.map(report =>
          this.reportsService.getComments(report.id).pipe(
            map(comments => ({
              ...report,
              status: this.normalizeStatus(report.status),   // ⭐ FIX
              commentCount: comments.length
            }))
          )
        );

        return forkJoin(calls);
      })
    ).subscribe({
      next: (finalReports: any[]) => {
        this.reports = finalReports.sort(
          (a, b) => new Date(b.reportDate).getTime() - new Date(a.reportDate).getTime()
        );
        this.loading = false;
      },
      error: () => {
        this.error = 'Failed to load reports.';
        this.loading = false;
      }
    });
  }

  openPanel(report: any) {
    if (this.selectedReport?.id === report.id) {
      this.closePanel();
      return;
    }

    this.selectedReport = {
      ...report,
      status: this.normalizeStatus(report.status)  // ⭐ FIX
    };

    this.panelOpen = true;
    this.commentsLoading = true;

    this.reportsService.getComments(report.id).subscribe({
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
    if (!this.newComment.trim() || !this.selectedReport || !this.currentUserId) return;

    const body = {
      text: this.newComment,
      userId: this.currentUserId
    };

    this.reportsService.addComment(this.selectedReport.id, body).subscribe({
      next: (createdComment) => {
        this.comments.push(createdComment);
        this.newComment = '';

        const report = this.reports.find(r => r.id === this.selectedReport.id);
        if (report) report.commentCount++;
      },
      error: err => console.error(err)
    });
  }

  getStatusColor(status: string): string {
    status = this.normalizeStatus(status); // ⭐ FIX

    switch (status) {
      case 'APPROVED': return '#4caf50';
      case 'REJECTED': return '#f44336';
      case 'IN_REVIEW': return '#ff9800';
      default: return '#cfd9e4';
    }
  }
}
