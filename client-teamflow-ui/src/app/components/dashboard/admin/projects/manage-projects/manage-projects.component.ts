// src/app/components/dashboard/admin/projects/manage-projects/manage-projects.component.ts

import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ProjectsService } from '../../../../../services/projects.service';
import { ProjectDTO } from '../../../../../models/project-dto.model';

@Component({
  selector: 'app-manage-projects',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './manage-projects.component.html',
  styleUrls: ['./manage-projects.component.css']
})
export class ManageProjectsComponent implements OnInit {

  projects: ProjectDTO[] = [];

  loading = false;
  error: string | null = null;
  saving = false;

  showAddForm = false;
  showEditForm = false;

  // פרויקט חדש
  newProject: Partial<ProjectDTO> = {
    name: '',
    description: '',
    status: 'ACTIVE',
    startDate: null,
    endDate: null,
    progress: 0,
    leaderName: '',
    categoryName: ''
  };

  // פרויקט לעריכה
  editingProject: ProjectDTO | null = null;

  constructor(private projectsService: ProjectsService) {}

  ngOnInit(): void {
    this.loadProjects();
  }

  // טעינת כל הפרויקטים
  loadProjects(): void {
    this.loading = true;
    this.error = null;

    this.projectsService.getAll().subscribe({
      next: (projects) => {
        this.projects = projects || [];
        this.loading = false;
      },
      error: () => {
        this.error = 'Failed to load projects.';
        this.loading = false;
      }
    });
  }

  // הוספת פרויקט חדש
  addProject(): void {
    if (!this.newProject.name || !this.newProject.name.trim()) {
      this.error = 'Project name is required.';
      return;
    }

    const payload: Partial<ProjectDTO> = {
      name: this.newProject.name.trim(),
      description: this.newProject.description?.trim() || '',
      status: this.newProject.status || 'ACTIVE',
      startDate: this.newProject.startDate ?? null,
      endDate: this.newProject.endDate ?? null,
      progress: this.newProject.progress ?? 0,
      leaderName: this.newProject.leaderName || '',
      categoryName: this.newProject.categoryName || ''
    };

    this.saving = true;
    this.projectsService.create(payload).subscribe({
      next: (created) => {
        this.projects = [...this.projects, created];
        this.newProject = {
          name: '',
          description: '',
          status: 'ACTIVE',
          startDate: null,
          endDate: null,
          progress: 0
        };
        this.showAddForm = false;
        this.saving = false;
      },
      error: (err) => {
        console.error('❌ Creation failed:', err);
        this.error = 'Failed to create project.';
        this.saving = false;
      }
    });
  }

  // פתיחת עריכה
  openEdit(project: ProjectDTO): void {
    this.editingProject = { ...project };
    this.showEditForm = true;
    this.error = null;
  }

  cancelEdit(): void {
    this.editingProject = null;
    this.showEditForm = false;
    this.saving = false;
  }

  // עדכון פרויקט
  updateProject(): void {
    if (!this.editingProject || this.editingProject.id == null) return;

    const id: number = this.editingProject.id;

    const payload: Partial<ProjectDTO> = {
      name: this.editingProject.name?.trim() || '',
      description: this.editingProject.description?.trim() || '',
      status: this.editingProject.status,
      startDate: this.editingProject.startDate ?? null,
      endDate: this.editingProject.endDate ?? null,
      progress: this.editingProject.progress ?? 0,
      leaderName: this.editingProject.leaderName,
      categoryName: this.editingProject.categoryName
    };

    this.saving = true;
    this.projectsService.update(id, payload).subscribe({
      next: (updated) => {
        const idx = this.projects.findIndex(p => p.id === updated.id);
        if (idx !== -1) {
          this.projects[idx] = { ...updated };
        }
        this.cancelEdit();
      },
      error: () => {
        this.error = 'Failed to update project.';
        this.saving = false;
      }
    });
  }

  // מחיקת פרויקט
  deleteProject(id: number | undefined): void {
    if (id == null) return;
    if (!confirm('Are you sure you want to delete this project?')) return;

    this.projectsService.delete(id).subscribe({
      next: () => {
        this.projects = this.projects.filter(p => p.id !== id);
      },
      error: () => {
        this.error = 'Failed to delete project.';
      }
    });
  }

  // עיצוב סטטוס
  getStatusClass(status: string | undefined | null): string {
    if (!status) return 'inactive';
    const s = status.toUpperCase();
    if (s === 'ACTIVE') return 'active';
    if (s === 'COMPLETED') return 'completed';
    if (s === 'ON_HOLD') return 'onhold';
    return 'inactive';
  }

  getStatusLabel(status: string | undefined | null): string {
    if (!status) return 'Inactive';
    const map: Record<string, string> = {
      ACTIVE: 'Active',
      COMPLETED: 'Completed',
      ON_HOLD: 'On Hold',
      INACTIVE: 'Inactive'
    };
    return map[status.toUpperCase()] || status;
  }
}
