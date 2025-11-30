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

  /** כל הצוותים מהשרת */
  teams: TeamDTO[] = [];

  /** עובדים זמינים לפי הצוות של ה-Leader */
  availableEmployeesNew: UsersDTO[] = [];
  availableEmployeesEdit: UsersDTO[] = [];

  loading = false;
  error: string | null = null;

  /** טופס הוספת פרויקט */
  showAddForm = false;
  saving = false;

  newProject: {
    id?: number;
    name: string;
    description: string;
    startDate: string | null;
    endDate: string | null;
    status: string;
    progress: number;
    location: string | null;
    leaderId: number | null;
    employeeIds: number[];
  } = {
    name: '',
    description: '',
    startDate: null,
    endDate: null,
    status: 'ACTIVE',
    progress: 0,
    location: null,
    leaderId: null,
    employeeIds: []
  };

  /** טופס עריכת פרויקט */
  showEditForm = false;
  editingProject: ProjectDTO | null = null;

  constructor(
    private projectsService: ProjectsService,
    private usersService: UsersService,
    private employeeProjectService: EmployeeProjectService,
    private teamService: TeamService
  ) {}

ngOnInit(): void {
  this.loadUsers();     // קודם משתמשים
  this.loadTeams();     // אחר־כך צוותים
  this.loadProjects();  // אחר־כך פרויקטים
}


  /* ------------------ LOAD DATA ------------------- */

  loadProjects(): void {
    this.loading = true;
    this.projectsService.getAll().subscribe({
      next: (data: ProjectDTO[]) => {
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
      next: (data: UsersDTO[]) => {
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
    next: (data: TeamDTO[]) => {
      this.teams = data || [];

      // המתנה קטנה כדי לוודא ש-ngModel הספיק להתעדכן
      setTimeout(() => {
        this.syncAvailableEmployeesForNew();
        this.syncAvailableEmployeesForEdit();
      }, 0);
    },
    error: () => {
      console.error('Failed to load teams');
    }
  });
}


  /* ------------------ HELPERS לצוותים ------------------- */

  private getTeamByLeader(leaderId: number | null | undefined): TeamDTO | null {
    if (leaderId == null) return null;
    return this.teams.find(t => t.leaderId === leaderId) || null;
  }

  /** עובדים זמינים בטופס הוספה */
  private syncAvailableEmployeesForNew(): void {
    const team = this.getTeamByLeader(this.newProject.leaderId);
    this.availableEmployeesNew = team?.members || [];

    // מנקה בחירות שלא שייכות לצוות
    this.newProject.employeeIds = this.newProject.employeeIds.filter(id =>
      this.availableEmployeesNew.some(emp => emp.id === id)
    );
  }

  /** עובדים זמינים בטופס עריכה */
  private syncAvailableEmployeesForEdit(): void {
    if (!this.editingProject) {
      this.availableEmployeesEdit = [];
      return;
    }

    const team = this.getTeamByLeader(this.editingProject.leaderId ?? null);
    this.availableEmployeesEdit = team?.members || [];

    this.editingProject.employeeIds = (this.editingProject.employeeIds ?? [])
      .filter(id => this.availableEmployeesEdit.some(emp => emp.id === id));
  }

  /* ------------------ ADD PROJECT ------------------- */

  /** כשמשנים Team Leader בטופס הוספה */
  onNewLeaderChange(leaderId: number | null): void {
    this.newProject.leaderId = leaderId;
    this.syncAvailableEmployeesForNew();
  }

  toggleEmployee(event: Event, id: number): void {
    const checked = (event.target as HTMLInputElement).checked;

    if (checked) {
      if (!this.newProject.employeeIds.includes(id)) {
        this.newProject.employeeIds.push(id);
      }
    } else {
      this.newProject.employeeIds =
        this.newProject.employeeIds.filter(item => item !== id);
    }
  }

  addProject(): void {
    if (!this.newProject.name || !this.newProject.leaderId) {
      alert('Project must have a name and a team leader');
      return;
    }

    this.saving = true;

    const payload: Partial<ProjectDTO> = {
      name: this.newProject.name,
      description: this.newProject.description,
      startDate: this.newProject.startDate || undefined,
      endDate: this.newProject.endDate || undefined,
      status: this.newProject.status,
      progressPercentage: this.newProject.progress,
      location: this.newProject.location || undefined,
      leaderId: this.newProject.leaderId || undefined,
      employeeIds: this.newProject.employeeIds
    };

    this.projectsService.create(payload).subscribe({
      next: (created: ProjectDTO) => {
        this.projects.unshift(created);
        this.resetAddForm();
        this.saving = false;
      },
      error: () => {
        alert('❌ Failed to create project');
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
      progress: 0,
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

  /** כשמשנים Leader בטופס עריכה */
  onEditLeaderChange(leaderId: number | null): void {
    if (!this.editingProject) return;
    this.editingProject.leaderId = leaderId ?? null;
    this.syncAvailableEmployeesForEdit();
  }

  toggleEmployeeEdit(event: Event, id: number): void {
    if (!this.editingProject) return;

    const checked = (event.target as HTMLInputElement).checked;

    if (!this.editingProject.employeeIds) {
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

  updateProject(): void {
    if (!this.editingProject || this.editingProject.id == null) return;

    this.saving = true;

    const payload: Partial<ProjectDTO> = {
      name: this.editingProject.name,
      description: this.editingProject.description,
      startDate: this.editingProject.startDate,
      endDate: this.editingProject.endDate,
      status: this.editingProject.status,
      progressPercentage: this.editingProject.progressPercentage,
      location: this.editingProject.location,
      leaderId: this.editingProject.leaderId,
      employeeIds: this.editingProject.employeeIds
    };

    this.projectsService.update(this.editingProject.id, payload).subscribe({
      next: (updated: ProjectDTO) => {
        this.projects = this.projects.map(p => p.id === updated.id ? updated : p);
        this.cancelEdit();
        this.saving = false;
      },
      error: () => {
        alert('❌ Failed to update project');
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

  deleteProject(id: number): void {
    if (!confirm('Delete project?')) return;

    this.projectsService.delete(id).subscribe({
      next: () => {
        this.projects = this.projects.filter(p => p.id !== id);
      },
      error: () => alert('❌ Failed to delete project')
    });
  }
}
