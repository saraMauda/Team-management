import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

import { ProjectsService } from '../../../../services/projects.service';
import { TeamService } from '../../../../services/team.service';
import { AuthService } from '../../../../services/auth.service';

import { ProjectDTO } from '../../../../models/project-dto.model';
import { TeamDTO } from '../../../../models/team-dto.model';
import { UsersDTO } from '../../../../models/users-dto.model';

@Component({
  selector: 'app-my-projects',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './team-projects.component.html',
  styleUrls: ['./team-projects.component.css']
})
export class TeamProjectsComponent implements OnInit {

  projects: ProjectDTO[] = [];
  loading = true;
  error: string | null = null;
  currentUserId: number | null = null;
  teams: TeamDTO[] = [];
  leaderTeamMembers: UsersDTO[] = [];
  editingProject: ProjectDTO | null = null;
  saving = false;

  constructor(
    private projectsService: ProjectsService,
    private teamService: TeamService,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    this.loadCurrentUser();
  }

  // ----------------------------------------------------
  // LOAD CURRENT USER
  // ----------------------------------------------------
  loadCurrentUser(): void {
    const stored = localStorage.getItem('user');
    if (!stored) {
      this.error = 'User not found.';
      this.loading = false;
      return;
    }

    const obj = JSON.parse(stored);
    const email = obj.email;

    if (!email) {
      this.error = 'User email not found.';
      this.loading = false;
      return;
    }

    this.authService.getUserByEmail(email).subscribe({
      next: (user) => {
        this.currentUserId = user.id;
        this.loadTeamsAndProjects();
      },
      error: () => {
        this.error = 'Failed to identify user.';
        this.loading = false;
      }
    });
  }

  // ----------------------------------------------------
  // LOAD TEAM + PROJECTS
  // ----------------------------------------------------
  loadTeamsAndProjects(): void {
    this.teamService.getAllTeams().subscribe({
      next: (data: TeamDTO[]) => {
        this.teams = data || [];
        const myTeam = this.teams.find(t => t.leaderId === this.currentUserId!) || null;
        this.leaderTeamMembers = myTeam?.members || [];
        this.loadProjectsForLeader();
      },
      error: () => {
        this.leaderTeamMembers = [];
        this.loadProjectsForLeader();
      }
    });
  }

  loadProjectsForLeader(): void {
    this.projectsService.getAll().subscribe({
      next: (data: ProjectDTO[]) => {
        const all = data || [];
        this.projects = all.filter(p => p.leaderId === this.currentUserId);
        this.loading = false;
      },
      error: () => {
        this.error = 'Failed to load projects';
        this.loading = false;
      }
    });
  }

  // ----------------------------------------------------
  // EDIT
  // ----------------------------------------------------
  openEdit(project: ProjectDTO): void {
    this.editingProject = {
      ...project,
      employeeIds: project.employeeIds ? [...project.employeeIds] : []
    };
  }

  cancelEdit(): void {
    this.editingProject = null;
  }

  toggleEmployee(event: Event, id: number): void {
    if (!this.editingProject) return;

    const checked = (event.target as HTMLInputElement).checked;

    if (!Array.isArray(this.editingProject.employeeIds)) {
      this.editingProject.employeeIds = [];
    }

    if (checked) {
      if (!this.editingProject.employeeIds.includes(id)) {
        this.editingProject.employeeIds.push(id);
      }
    } else {
      this.editingProject.employeeIds =
        this.editingProject.employeeIds.filter(empId => empId !== id);
    }
  }

  // ----------------------------------------------------
 // UPDATE PROJECT 
  // ----------------------------------------------------
  updateProject(): void {
    if (!this.editingProject?.id) return;

    this.saving = true;

    const payload: ProjectDTO = {
      id: this.editingProject.id,
      name: this.editingProject.name,
      description: this.editingProject.description,
      startDate: this.editingProject.startDate,
      endDate: this.editingProject.endDate,
      status: this.editingProject.status,
      progressPercentage: this.editingProject.progressPercentage ?? 0,
      employeeIds: this.editingProject.employeeIds || [],
      leaderId: this.currentUserId!, // ✔ חובה לפי השרת
      location: this.editingProject.location ?? null,
      leaderName: this.editingProject.leaderName ?? null
    };

    this.projectsService.update(this.editingProject.id, payload).subscribe({
      next: (updated) => {
        this.projects = this.projects.map(p =>
          p.id === updated.id ? updated : p
        );
        this.editingProject = null;
        this.saving = false;
      },
      error: () => {
        alert('❌ Failed to update project');
        this.saving = false;
      }
    });
  }

  // ----------------------------------------------------
  // DELETE PROJECT — ✔ 
  // ----------------------------------------------------
  deleteProject(id: number): void {
    if (!confirm('Delete project?')) return;

    this.projectsService.delete(id).subscribe({
      next: () => {
        this.projects = this.projects.filter(p => p.id !== id);
      },
      error: () => {
        alert('❌ Failed to delete project');
      }
    });
  }

  // ----------------------------------------------------
  // UTIL
  // ----------------------------------------------------
  getProgressColor(progress: number | null | undefined): string {
    if (progress == null) return '#999';
    if (progress >= 80) return '#4caf50';
    if (progress >= 50) return '#ff9800';
    return '#f44336';
  }
}
