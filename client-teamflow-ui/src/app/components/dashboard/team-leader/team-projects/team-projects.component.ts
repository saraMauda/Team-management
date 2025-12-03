
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

  toastVisible = false;
  toastMessage = '';
  toastType: 'success' | 'error' = 'success';

  constructor(
    private projectsService: ProjectsService,
    private teamService: TeamService,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    this.loadCurrentUser();
  }

  showToast(msg: string, type: 'success' | 'error' = 'success') {
    this.toastMessage = msg;
    this.toastType = type;
    this.toastVisible = true;
    setTimeout(() => (this.toastVisible = false), 5000);
  }

  loadCurrentUser(): void {
    const stored = localStorage.getItem('user');
    if (!stored) {
      this.error = 'User not found.';
      this.loading = false;
      return;
    }

    const email = JSON.parse(stored).email ?? "";
    if (!email) {
      this.error = 'User email not found.';
      this.loading = false;
      return;
    }

    this.authService.getUserByEmail(email).subscribe({
      next: user => {
        this.currentUserId = user.id;
        this.loadTeamsAndProjects();
      },
      error: () => {
        this.error = 'Failed to identify user.';
        this.loading = false;
      }
    });
  }

  loadTeamsAndProjects(): void {
    this.teamService.getAllTeams().subscribe({
      next: teams => {
        this.teams = teams;
        const myTeam = teams.find(t => t.leaderId === this.currentUserId) ?? null;
        this.leaderTeamMembers = myTeam?.members ?? [];
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
      next: data => {
        this.projects = data.filter(p => p.leaderId === this.currentUserId);
        this.loading = false;
      },
      error: () => {
        this.error = 'Failed to load projects';
        this.loading = false;
      }
    });
  }

  openEdit(p: ProjectDTO) {
    this.editingProject = {
      ...p,
      employeeIds: [...(p.employeeIds ?? [])]
    };
  }

  cancelEdit() {
    this.editingProject = null;
  }

  toggleEmployee(event: Event, id: number) {
    if (!this.editingProject) return;

    const checked = (event.target as HTMLInputElement).checked;

    if (!Array.isArray(this.editingProject.employeeIds)) {
      this.editingProject.employeeIds = [];
    }

    if (checked) {
      if (!this.editingProject.employeeIds.includes(id))
        this.editingProject.employeeIds.push(id);
    } else {
      this.editingProject.employeeIds =
        this.editingProject.employeeIds.filter(e => e !== id);
    }
  }

  isDateRangeInvalid(): boolean {
    if (!this.editingProject?.startDate || !this.editingProject?.endDate) return false;
    return new Date(this.editingProject.endDate) < new Date(this.editingProject.startDate);
  }

  isEditFormValid(): boolean {
    if (!this.editingProject) return false;

    const name = this.editingProject.name ?? "";
    const desc = this.editingProject.description ?? "";
    const progress = this.editingProject.progressPercentage ?? 0;
    const status = this.editingProject.status ?? "";

    const nameValid = name.trim().length >= 3 && name.trim().length <= 40;
    const descValid = desc.trim() === "" || desc.trim().length >= 10;
    const startValid = !!this.editingProject.startDate;
    const endValid = !!this.editingProject.endDate;
    const datesValid = startValid && endValid && !this.isDateRangeInvalid();
    const statusValid = ['ACTIVE', 'COMPLETED', 'ON_HOLD'].includes(status);
    const progressValid = progress >= 0 && progress <= 100;
    const employeesValid = Array.isArray(this.editingProject.employeeIds);

    return (
      nameValid &&
      descValid &&
      startValid &&
      endValid &&
      datesValid &&
      progressValid &&
      statusValid &&
      employeesValid
    );
  }

  updateProject() {
    if (!this.editingProject?.id) return;

    if (!this.isEditFormValid()) {
      this.showToast('Please fix validation errors.', 'error');
      return;
    }

    if (!this.currentUserId) {
      this.showToast('Cannot update: user not identified.', 'error');
      return;
    }

    this.saving = true;

    const payload: ProjectDTO = {
      ...this.editingProject,
      leaderId: this.currentUserId
    };

    this.projectsService.update(this.editingProject.id, payload).subscribe({
      next: updated => {
        this.projects = this.projects.map(p => (p.id === updated.id ? updated : p));
        this.editingProject = null;
        this.saving = false;
        this.showToast('Project updated successfully.', 'success');
      },
      error: () => {
        this.showToast('Failed to update project.', 'error');
        this.saving = false;
      }
    });
  }

  deleteProject(id: number) {
    this.projectsService.delete(id).subscribe({
      next: () => {
        this.projects = this.projects.filter(p => p.id !== id);
        this.showToast('Project deleted successfully.', 'success');
      },
      error: () => {
        this.showToast('Failed to delete project.', 'error');
      }
    });
  }

  getProgressColor(progress: number | null | undefined): string {
    const val = progress ?? 0;
    if (val >= 80) return '#4caf50';
    if (val >= 50) return '#ff9800';
    if (val >= 0) return '#f44336';
    return '#999';
  }
}
