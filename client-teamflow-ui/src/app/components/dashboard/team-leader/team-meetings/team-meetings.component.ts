import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpClient } from '@angular/common/http';

import { API_BASE_URL } from '../../../../app.config';
import { AuthService } from '../../../../services/auth.service';
import { UsersService } from '../../../../services/users.service';
import { ProjectsService } from '../../../../services/projects.service';

@Component({
  selector: 'app-team-meetings',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './team-meetings.component.html',
  styleUrls: ['./team-meetings.component.css']
})
export class TeamMeetingsComponent implements OnInit {

  leaderId: number | null = null;

  meetings: any[] = [];
  projects: any[] = [];
  leaderProjects: any[] = [];

  showAddForm = false;

  newMeeting = {
    projectId: 0,
    title: '',
    description: '',
    meetingLocation: '',
    meetingDate: '',
    status: 'SCHEDULED'
  };

  // Toast
  toastVisible = false;
  toastMessage = '';
  toastType: 'success' | 'error' = 'success';

  constructor(
    private http: HttpClient,
    private auth: AuthService,
    private usersService: UsersService,
    private projectsService: ProjectsService
  ) { }

  ngOnInit(): void {
    this.loadCurrentLeader();
  }

  // ---------------- TOAST ----------------

  showToast(message: string, type: 'success' | 'error' = 'success'): void {
    this.toastMessage = message;
    this.toastType = type;
    this.toastVisible = true;

    setTimeout(() => {
      this.toastVisible = false;
    }, 5000);
  }

  // ---------------- LOAD DATA ----------------

  loadCurrentLeader(): void {
    const stored = localStorage.getItem('user');
    if (!stored) {
      this.showToast('User is not logged in.', 'error');
      return;
    }

    const obj = JSON.parse(stored);
    const email = obj.email;

    this.auth.getUserByEmail(email).subscribe({
      next: user => {
        this.leaderId = user.id;
        this.loadProjectsForLeader();
        this.loadMeetings();
      },
      error: () => {
        this.showToast('Failed to load leader details.', 'error');
      }
    });
  }

  loadProjectsForLeader(): void {
    this.projectsService.getAll().subscribe({
      next: (all: any[]) => {
        this.projects = all || [];
        this.leaderProjects = this.projects.filter(p => p.leaderId === this.leaderId);
      },
      error: () => {
        this.showToast('Failed to load projects.', 'error');
      }
    });
  }

  loadMeetings(): void {
    this.http.get<any[]>(`${API_BASE_URL}/meetings`, { withCredentials: true }).subscribe({
      next: (data) => {
        this.meetings = data || [];
      },
      error: err => {
        this.showToast('Failed to load meetings.', 'error');
      }
    });
  }

  // ---------------- VALIDATION ----------------

  isMeetingFormValid(): boolean {
    const titleValid =
      !!this.newMeeting.title &&
      this.newMeeting.title.trim().length >= 3;

    const dateValid =
      !!this.newMeeting.meetingDate;

    const projectValid =
      this.newMeeting.projectId !== 0;

    const locationValid =
      !this.newMeeting.meetingLocation ||
      this.newMeeting.meetingLocation.trim().length >= 3;

    const descValid =
      !this.newMeeting.description ||
      this.newMeeting.description.trim().length >= 10;

    return titleValid && dateValid && projectValid && locationValid && descValid;
  }

  // ---------------- CREATE MEETING ----------------

  createMeeting(): void {
    // חסימת שליחה אם לא תקין
    if (!this.isMeetingFormValid()) {
      this.showToast('Please fix validation errors.', 'error');
      return;
    }

    const payload = {
      ...this.newMeeting,
      title: this.newMeeting.title.trim(),
      description: this.newMeeting.description.trim(),
      meetingLocation: this.newMeeting.meetingLocation.trim()
    };

    this.http.post(`${API_BASE_URL}/meetings/create`, payload, {
      withCredentials: true
    }).subscribe({
      next: () => {
        this.showToast('Meeting created successfully.', 'success');
        this.toggleAddForm();
        this.loadMeetings();

        // reset form
        this.newMeeting = {
          projectId: 0,
          title: '',
          description: '',
          meetingLocation: '',
          meetingDate: '',
          status: 'SCHEDULED'
        };
      },
      error: err => {
        this.showToast('Failed to create meeting.', 'error');
      }
    });
  }

  toggleAddForm(): void {
    this.showAddForm = !this.showAddForm;
  }
}
