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
  leaderProjects: any[] = [];

  showAddForm = false;

  // NEW MEETING MODEL
  newMeeting = {
    projectId: 0,
    title: '',
    description: '',
    meetingLocation: '',
    meetingDate: '',
    status: 'SCHEDULED'
  };

  // EDITING MEETING
  editingMeeting: any = null;

  // Toast
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
    }, 4000);
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
        this.loadProjects();
        this.loadMeetings();
      },
      error: () => this.showToast('Failed to load leader data', 'error')
    });
  }

  loadProjects(): void {
    this.projectsService.getAll().subscribe({
      next: (all: any[]) => {
        this.leaderProjects = (all || []).filter(p => p.leaderId === this.leaderId);
      },
      error: () => this.showToast('Failed to load projects', 'error')
    });
  }

  loadMeetings(): void {
    this.http.get<any[]>(`${API_BASE_URL}/meetings`, {
      withCredentials: true
    }).subscribe({
      next: data => this.meetings = data || [],
      error: () => this.showToast('Failed to load meetings', 'error')
    });
  }

  // ---------------- VALIDATION ----------------

  isMeetingFormValid(): boolean {
    return (
      this.newMeeting.projectId !== 0 &&
      this.newMeeting.title.trim().length >= 3 &&
      !!this.newMeeting.meetingDate &&
      (this.newMeeting.meetingLocation.trim().length === 0 ||
        this.newMeeting.meetingLocation.trim().length >= 3) &&
      (this.newMeeting.description.trim().length === 0 ||
        this.newMeeting.description.trim().length >= 10)
    );
  }

  isEditMeetingValid(): boolean {
    return (
      this.editingMeeting &&
      this.editingMeeting.projectId &&
      (this.editingMeeting.title || '').trim().length >= 3 &&
      !!this.editingMeeting.meetingDate &&
      ((this.editingMeeting.meetingLocation || '').trim().length === 0 ||
        (this.editingMeeting.meetingLocation || '').trim().length >= 3) &&
      ((this.editingMeeting.description || '').trim().length === 0 ||
        (this.editingMeeting.description || '').trim().length >= 10)
    );
  }

  // ---------------- CREATE ----------------

  createMeeting(): void {
    if (!this.isMeetingFormValid()) {
      this.showToast('Fix validation errors', 'error');
      return;
    }

    const payload = {
      ...this.newMeeting,
      title: this.newMeeting.title.trim()
    };

    this.http.post(`${API_BASE_URL}/meetings/create`, payload, {
      withCredentials: true
    }).subscribe({
      next: () => {
        this.showToast('Meeting created', 'success');
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
      error: () => this.showToast('Failed to create meeting', 'error')
    });
  }

  toggleAddForm(): void {
    this.showAddForm = !this.showAddForm;
  }

  // ---------------- EDIT ----------------

  startEdit(m: any): void {
    this.editingMeeting = { ...m };
  }

  cancelEdit(): void {
    this.editingMeeting = null;
  }

  saveEdit(): void {
    if (!this.isEditMeetingValid()) {
      this.showToast('Fix validation errors', 'error');
      return;
    }

    this.http.put(`${API_BASE_URL}/meetings/${this.editingMeeting.meetingId}`,
      this.editingMeeting,
      { withCredentials: true }
    ).subscribe({
      next: () => {
        this.showToast('Meeting updated', 'success');
        this.editingMeeting = null;
        this.loadMeetings();
      },
      error: () => this.showToast('Failed to update meeting', 'error')
    });
  }

  // ---------------- DELETE ----------------

  deleteMeeting(id: number): void {
    if (!confirm('Are you sure you want to delete this meeting?')) return;

    this.http.delete(`${API_BASE_URL}/meetings/${id}`, {
      withCredentials: true
    }).subscribe({
      next: () => {
        this.showToast('Meeting deleted', 'success');
        this.loadMeetings();
      },
      error: () => this.showToast('Failed to delete meeting', 'error')
    });
  }
}
