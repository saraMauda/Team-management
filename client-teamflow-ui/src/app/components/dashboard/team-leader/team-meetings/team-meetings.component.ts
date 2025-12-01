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

  // 🔔 Toast
  toastVisible = false;
  toastMessage = '';
  toastType: 'success' | 'error' = 'success';

  constructor(
    private http: HttpClient,
    private auth: AuthService,
    private usersService: UsersService,
    private projectsService: ProjectsService
  ) {}

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
      console.error('❌ No user in localStorage');
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
        console.error('❌ Cannot load leader user');
        this.showToast('Failed to load leader details.', 'error');
      }
    });
  }

  loadProjectsForLeader(): void {
    this.projectsService.getAll().subscribe({
      next: (all: any[]) => {
        this.projects = all || [];
        this.leaderProjects = this.projects.filter(p => p.leaderId === this.leaderId);
        console.log('Leader Projects:', this.leaderProjects);
      },
      error: () => {
        console.error('❌ Failed loading all projects');
        this.showToast('Failed to load projects.', 'error');
      }
    });
  }

  loadMeetings(): void {
    this.http.get<any[]>(`${API_BASE_URL}/meetings`, {
      withCredentials: true
    }).subscribe({
      next: (data) => {
        this.meetings = data || [];
      },
      error: err => {
        console.error('❌ Error loading meetings:', err);
        this.showToast('Failed to load meetings.', 'error');
      }
    });
  }

  // ---------------- FORM ACTIONS ----------------

  toggleAddForm(): void {
    this.showAddForm = !this.showAddForm;
  }

  createMeeting(): void {
    // ולידציה – הכל עם Toast, בלי alert
    if (this.newMeeting.projectId === 0) {
      this.showToast('Please select a project.', 'error');
      return;
    }

    if (!this.newMeeting.title.trim()) {
      this.showToast('Please enter a meeting title.', 'error');
      return;
    }

    if (!this.newMeeting.meetingDate) {
      this.showToast('Please select a meeting date.', 'error');
      return;
    }

    if (!this.newMeeting.description.trim()) {
      this.showToast('Please enter a meeting description.', 'error');
      return;
    }

    this.http.post(`${API_BASE_URL}/meetings/create`, this.newMeeting, {
      withCredentials: true
    }).subscribe({
      next: () => {
        this.showToast('Meeting created successfully.', 'success');
        this.toggleAddForm();
        this.loadMeetings();

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
        console.error('❌ Error creating meeting:', err);
        this.showToast('Failed to create meeting.', 'error');
      }
    });
  }
}
