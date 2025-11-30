import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MeetingsService } from '../../../../services/meetings.service';
import { ProjectsService } from '../../../../services/projects.service';
import { ApprovalService } from '../../../../services/approval.service';

@Component({
  selector: 'app-meetings',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './meetings.component.html',
  styleUrls: ['./meetings.component.css']
})
export class MeetingsComponent implements OnInit {

  meetings: any[] = [];
  loading = true;
  error: string | null = null;

  constructor(
    private meetingsService: MeetingsService,
    private projectsService: ProjectsService,
    private approvalService: ApprovalService
  ) { }

  ngOnInit(): void {
    this.loadMeetings();
  }

  loadMeetings(): void {
    this.meetingsService.getMyMeetings().subscribe({
      next: meetings => {
        this.projectsService.getAll().subscribe({
          next: projects => {


            const enriched = meetings.map(m => {
              const p = projects.find(p =>
                p.id === (m as any).projectId
              );


              return {
                ...m,
                projectName: p?.name || null,
                approvalCount: 0,
                alreadyApproved: false
              };
            });

            this.meetings = enriched;

           
            this.loadApprovalsForAllMeetings();

            this.loading = false;
          },
          error: () => this.loading = false
        });
      },
      error: () => {
        this.error = 'Failed to load meetings';
        this.loading = false;
      }
    });
  }

 
  loadApprovalsForAllMeetings(): void {
    const userId = this.getUserIdFromCookie();

    this.meetings.forEach(m => {
      this.approvalService.getApprovalsByMeeting(m.meetingId).subscribe({
        next: approvals => {

          m.approvalCount = approvals.length;

          m.alreadyApproved = approvals.some(a =>
            a.employeeInProjectId === userId
          );
        }
      });
    });
  }

  approveMeeting(meetingId: number): void {

    const body = {
      approved: true,
      meeting: { meetingId },
      approvalEmployeeInProject: { employeeProjectId: this.getUserIdFromCookie() }
    };

    this.approvalService.createApproval(body).subscribe({
      next: () => {
        const meeting = this.meetings.find(m => m.meetingId === meetingId);
        if (meeting) {
          meeting.alreadyApproved = true;
          meeting.approvalCount++;
        }
      }
    });
  }

  getUserIdFromCookie(): number {
    const c = document.cookie.split('; ')
      .find(row => row.startsWith('userId='));
    return c ? Number(c.split('=')[1]) : -1;
  }

  isPast(date: string): boolean {
    return new Date(date) < new Date();
  }

  formatDate(date: string): string {
    return new Date(date).toLocaleString();
  }
}
