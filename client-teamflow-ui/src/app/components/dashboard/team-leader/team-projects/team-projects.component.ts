import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ProjectsService } from '../../../../services/projects.service';
import { ProjectDTO } from '../../../../models/project-dto.model';

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

  /** מי המשתמש */
  currentUserId: number | null = null;

  /** עריכה */
  showEditForm = false;
  editingProject: ProjectDTO | null = null;
  saving = false;

  constructor(private projectsService: ProjectsService) {}

  ngOnInit(): void {
    this.loadCurrentUser();
    this.loadProjects();
  }

  loadCurrentUser(): void {
    const stored = localStorage.getItem('user');
    if (stored) {
      const obj = JSON.parse(stored);
      this.currentUserId = obj.id ?? null;
    }
  }

  loadProjects(): void {
    this.projectsService.getAll().subscribe({
      next: (data) => {
        this.projects = this.filterMyProjects(data);
        this.loading = false;
      },
      error: () => {
        this.error = 'Failed to load projects';
        this.loading = false;
      }
    });
  }

  /** מציג לעובד רק פרויקטים שהוא חלק מהם */
  private filterMyProjects(list: ProjectDTO[]): ProjectDTO[] {
    if (!this.currentUserId) return [];
    return list.filter(p =>
      Array.isArray(p.employeeIds) &&
      p.employeeIds.includes(this.currentUserId!)
    );
  }

  /** פותח את הפאנל עריכה */
  openEdit(project: ProjectDTO): void {
    this.editingProject = { ...project };
    this.showEditForm = true;
  }

  /** עדכון בפועל */
  updateProject(): void {
    if (!this.editingProject?.id) return;

    this.saving = true;

    const payload: Partial<ProjectDTO> = {
      name: this.editingProject.name,
      description: this.editingProject.description,
      startDate: this.editingProject.startDate,
      endDate: this.editingProject.endDate,
      progress: this.editingProject.progress,
      status: this.editingProject.status
    };

    this.projectsService.update(this.editingProject.id, payload).subscribe({
      next: (updated) => {
        this.projects = this.projects.map(p =>
          p.id === updated.id ? updated : p
        );
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
  }

  /** צבע */
  getProgressColor(progress: number | null | undefined): string {
    if (progress == null) return '#999';
    if (progress >= 80) return '#4caf50';
    if (progress >= 50) return '#ff9800';
    return '#f44336';
  }
}
