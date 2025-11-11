import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ProjectsService } from '../../../../services/projects.service';

@Component({
  selector: 'app-team-projects',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './team-projects.component.html',
  styleUrls: ['./team-projects.component.css']
})
export class TeamProjectsComponent implements OnInit {
  projects: any[] = [];
  loading = true;
  error: string | null = null;

  constructor(private projectsService: ProjectsService) {}

  ngOnInit(): void {
    this.loadProjects();
  }

  loadProjects(): void {
    this.projectsService.getAll().subscribe({
      next: (data) => {
        // 🔹 כאן אפשר לסנן רק את הפרויקטים של ראש הצוות
        this.projects = data.filter((p: any) => p.status !== 'ARCHIVED');
        this.loading = false;
      },
      error: (err) => {
        console.error('❌ Failed to load team projects', err);
        this.error = 'Failed to load projects';
        this.loading = false;
      }
    });
  }

  getProgressColor(progress: number): string {
    if (progress >= 80) return '#4caf50';
    if (progress >= 50) return '#ff9800';
    return '#f44336';
  }
}
