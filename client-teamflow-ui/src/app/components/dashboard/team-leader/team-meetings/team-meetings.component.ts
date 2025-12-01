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

  constructor(
    private http: HttpClient,
    private auth: AuthService,
    private usersService: UsersService,
    private projectsService: ProjectsService
  ) { }

  ngOnInit(): void {
    this.loadCurrentLeader();
  }

  loadCurrentLeader(): void {
    const stored = localStorage.getItem('user');
    if (!stored) {
      console.error("❌ No user in localStorage");
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
      error: () => console.error("❌ Cannot load leader user")
    });
  }

  loadProjectsForLeader(): void {
    this.projectsService.getAll().subscribe({
      next: (all: any[]) => {
        this.projects = all || [];
        this.leaderProjects = this.projects.filter(p => p.leaderId === this.leaderId);
        console.log("Leader Projects:", this.leaderProjects);
      },
      error: () => console.error("❌ Failed loading all projects")
    });
  }

  loadMeetings() {
    this.http.get<any[]>(`${API_BASE_URL}/meetings`, {
      withCredentials: true
    }).subscribe({
      next: (data) => {
        this.meetings = data;
      },
      error: err => console.error("❌ Error loading meetings:", err)
    });
  }

  toggleAddForm() {
    this.showAddForm = !this.showAddForm;
  }

  createMeeting() {
    if (this.newMeeting.projectId === 0) {
      alert("Please select a project!");
      return;
    }

    this.http.post(`${API_BASE_URL}/meetings/create`, this.newMeeting, {
      withCredentials: true
    }).subscribe({
      next: () => {
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
      error: err => console.error("❌ Error creating meeting:", err)
    });
  }
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

}

