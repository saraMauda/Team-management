import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

import { ProjectsService } from '../../../../../services/projects.service';
import { UsersService } from '../../../../../services/users.service';
import { EmployeeProjectService } from '../../../../../services/employee-project.service';
import { TeamService } from '../../../../../services/team.service';

import { ProjectDTO } from '../../../../../models/project-dto.model';
import { UsersDTO } from '../../../../../models/users-dto.model';
import { TeamDTO } from '../../../../../models/team-dto.model';

@Component({
  selector: 'app-manage-projects',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './manage-projects.component.html',
  styleUrls: ['./manage-projects.component.css']
})
export class ManageProjectsComponent implements OnInit {

  projects: ProjectDTO[] = [];
  users: UsersDTO[] = [];
  teamLeaders: UsersDTO[] = [];
  employees: UsersDTO[] = [];

  teams: TeamDTO[] = [];

  availableEmployeesNew: UsersDTO[] = [];
  availableEmployeesEdit: UsersDTO[] = [];

  loading = false;
  error: string | null = null;

  showAddForm = false;
  saving = false;

  toastVisible = false;
  toastMessage = '';
  toastType: 'success' | 'error' = 'success';

  confirmDeleteId: number | null = null;

  newProject = {
    name: '',
    description: '',
    startDate: null as string | null,
    endDate: null as string | null,
    status: 'ACTIVE',
    progressPercentage: 0,
    location: null as string | null,
    leaderId: null as number | null,
    employeeIds: [] as number[]
  };

  showEditForm = false;
  editingProject: ProjectDTO | null = null;

  constructor(
    private projectsService: ProjectsService,
    private usersService: UsersService,
    private employeeProjectService: EmployeeProjectService,
    private teamService: TeamService
  ) {}

  ngOnInit(): void {
    this.loadUsers();
    this.loadTeams();
    this.loadProjects();
  }

  showToast(msg: string, type: 'success' | 'error' = 'success') {
    this.toastMessage = msg;
    this.toastType = type;
    this.toastVisible = true;

    setTimeout(() => this.toastVisible = false, 5000);
  }

  /* ------------------ LOAD DATA ------------------- */

  loadProjects(): void {
    this.loading = true;
    this.projectsService.getAll().subscribe({
      next: (data) => {
        this.projects = data;
        this.loading = false;
      },
      error: () => {
        this.error = 'Failed to load projects';
        this.loading = false;
      }
    });
  }

  loadUsers(): void {
    this.usersService.getAllUsers().subscribe({
      next: (data) => {
        this.users = data;
        this.teamLeaders = data.filter(u => u.role === 'ROLE_TEAMLEADER');
        this.employees = data.filter(u => u.role === 'ROLE_EMPLOYEE');
      },
      error: () => {
        this.error = 'Failed to load users';
      }
    });
  }

  loadTeams(): void {
    this.teamService.getAllTeams().subscribe({
      next: (data) => {
        this.teams = data || [];
        setTimeout(() => {
          this.syncAvailableEmployeesForNew();
          this.syncAvailableEmployeesForEdit();
        }, 0);
      },
      error: () => console.error('Failed to load teams')
    });
  }

  /* ------------------ TEAM HELPERS ------------------- */

  private getTeamByLeader(leaderId: number | null): TeamDTO | null {
    if (!leaderId) return null;
    return this.teams.find(t => t.leaderId === leaderId) || null;
  }

  private syncAvailableEmployeesForNew(): void {
    const team = this.getTeamByLeader(this.newProject.leaderId);
    this.availableEmployeesNew = team?.members || [];
  }

  private syncAvailableEmployeesForEdit(): void {
    if (!this.editingProject) return;
    const team = this.getTeamByLeader(this.editingProject.leaderId ?? null);
    this.availableEmployeesEdit = team?.members || [];
  }

  /* ------------------ ADD PROJECT ------------------- */

  onNewLeaderChange(leaderId: number | null): void {
    this.newProject.leaderId = leaderId;
    this.syncAvailableEmployeesForNew();
  }

  toggleEmployee(event: Event, id: number): void {
    const checked = (event.target as HTMLInputElement).checked;
    if (checked && !this.newProject.employeeIds.includes(id)) {
      this.newProject.employeeIds.push(id);
    } else if (!checked) {
      this.newProject.employeeIds = this.newProject.employeeIds.filter(x => x !== id);
    }
  }

  addProject(): void {
    if (!this.newProject.name || !this.newProject.leaderId) {
      this.showToast('Project must have a name and leader.', 'error');
      return;
    }

    this.saving = true;

    this.projectsService.create({ ...this.newProject }).subscribe({
      next: (created) => {
        this.projects.unshift(created);
        this.resetAddForm();
        this.showToast('Project added successfully!', 'success');
        this.saving = false;
      },
      error: () => {
        this.showToast('Failed to create project', 'error');
        this.saving = false;
      }
    });
  }

  resetAddForm(): void {
    this.newProject = {
      name: '',
      description: '',
      startDate: null,
      endDate: null,
      status: 'ACTIVE',
      progressPercentage: 0,
      location: null,
      leaderId: null,
      employeeIds: []
    };
    this.availableEmployeesNew = [];
    this.showAddForm = false;
  }

  /* ------------------ EDIT PROJECT ------------------- */

  openEdit(project: ProjectDTO): void {
    this.showAddForm = false;
    this.editingProject = {
      ...project,
      employeeIds: [...(project.employeeIds || [])]
    };
    this.syncAvailableEmployeesForEdit();
    this.showEditForm = true;
  }

  toggleEmployeeEdit(event: Event, id: number): void {
    if (!this.editingProject) return;

    const checked = (event.target as HTMLInputElement).checked;

    if (checked) {
      if (!this.editingProject.employeeIds!.includes(id)) {
        this.editingProject.employeeIds!.push(id);
      }
    } else {
      this.editingProject.employeeIds =
        this.editingProject.employeeIds!.filter(x => x !== id);
    }
  }

  updateProject(): void {
    if (!this.editingProject || this.editingProject.id == null) return;

    this.saving = true;

    this.projectsService.update(this.editingProject.id, this.editingProject).subscribe({
      next: (updated) => {
        this.projects = this.projects.map(p => p.id === updated.id ? updated : p);
        this.cancelEdit();
        this.saving = false;
        this.showToast('Project updated successfully!', 'success');
      },
      error: () => {
        this.showToast('Failed to update project', 'error');
        this.saving = false;
      }
    });
  }

  cancelEdit(): void {
    this.showEditForm = false;
    this.editingProject = null;
    this.availableEmployeesEdit = [];
  }

  /* ------------------ DELETE ------------------- */

  openDeleteConfirm(id: number): void {
    this.confirmDeleteId = id;
  }

  deleteProjectConfirmed(): void {
    if (!this.confirmDeleteId) return;

    const id = this.confirmDeleteId;

    this.projectsService.delete(id).subscribe({
      next: () => {
        this.projects = this.projects.filter(p => p.id !== id);
        this.confirmDeleteId = null;
        this.showToast('Project deleted successfully!', 'success');
      },
      error: () => {
        this.confirmDeleteId = null;
        this.showToast('Failed to delete project', 'error');
      }
    });
  }

  closeDeleteConfirm(): void {
    this.confirmDeleteId = null;
  }

  /* ------------------ VALIDATION ------------------- */

  isDateRangeValid(): boolean {
    if (!this.newProject.startDate || !this.newProject.endDate) return true;
    return new Date(this.newProject.endDate) >= new Date(this.newProject.startDate);
  }

  isNewProjectFormValid(): boolean {
    return (
      this.newProject.name.trim().length >= 3 &&
      this.newProject.leaderId !== null &&
      this.isDateRangeValid()
    );
  }

  isEditProjectFormValid(): boolean {
    if (!this.editingProject) return false;
    return true;
  }


onEditLeaderChange(leaderId: number | null): void {
  if (!this.editingProject) return;
  this.editingProject.leaderId = leaderId ?? null;
  this.syncAvailableEmployeesForEdit();
}

}
