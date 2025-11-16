import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ProjectsService } from '../../../../services/projects.service';
import { UsersService } from '../../../../services/users.service';

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

  loggedEmail: string | null = null;
  loggedUserId: number | null = null;

  constructor(
    private projectsService: ProjectsService,
    private usersService: UsersService
  ) {}

  ngOnInit(): void {
    this.loadLoggedUser();
  }

  loadLoggedUser(): void {
    const userJson = localStorage.getItem('user');

    if (!userJson) {
      this.error = 'User not found';
      this.loading = false;
      return;
    }

    const user = JSON.parse(userJson);
    this.loggedEmail = user.email ?? null;

    if (!this.loggedEmail) {
      this.error = 'Email missing';
      this.loading = false;
      return;
    }

    this.usersService.getByEmail(this.loggedEmail).subscribe({
      next: (u) => {
        this.loggedUserId = u.id;
        this.loadProjects();
      },
      error: () => {
        this.error = 'Cannot load user details';
        this.loading = false;
      }
    });
  }

  loadProjects(): void {
    this.projectsService.getAll().subscribe({
      next: (data) => {
        this.projects = data.filter(p =>
          p.employeeIds && p.employeeIds.includes(this.loggedUserId!)
        );
        this.loading = false;
      },
      error: () => {
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
