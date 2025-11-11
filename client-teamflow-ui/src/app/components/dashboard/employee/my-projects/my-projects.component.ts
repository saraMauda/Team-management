import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ProjectsService } from '../../../../services/projects.service';

@Component({
  selector: 'app-my-projects',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './my-projects.component.html',
  styleUrls: ['./my-projects.component.css']
})
export class MyProjectsComponent implements OnInit {
  projects: any[] = [];
  loading = true;
  error: string | null = null;

  constructor(private projectsService: ProjectsService) {}

  ngOnInit(): void {
    this.loadProjects();
  }

loadProjects(): void {
  this.projectsService.getMyProjects().subscribe({
    next: (data) => {
      this.projects = data;
      this.loading = false;
    },
    error: (err) => {
      console.error('❌ Failed to load projects', err);
      this.error = 'Failed to load your projects.';
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
