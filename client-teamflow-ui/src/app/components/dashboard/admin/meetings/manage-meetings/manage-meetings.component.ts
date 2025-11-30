import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';

import { MeetingsService } from '../../../../../services/meetings.service';
import { ProjectsService } from '../../../../../services/projects.service'; 
import { MeetingDTO } from '../../../../../models/meeting-dto.model';
import { FormsModule } from '@angular/forms';
import { forkJoin } from 'rxjs'; 

@Component({
  selector: 'app-manage-meetings',
  standalone: true,
imports: [CommonModule, FormsModule],
  templateUrl: './manage-meetings.component.html',
  styleUrls: ['./manage-meetings.component.css']
})
export class ManageMeetingsComponent implements OnInit {

  meetings: MeetingDTO[] = [];
  loading = true;
  error: string | null = null;
  editMode = false;

  
  projectNamesMap: Map<number, string> = new Map(); 

  constructor(
    private meetingsService: MeetingsService,
    private projectsService: ProjectsService 
  ) {}

  ngOnInit(): void {
    this.loadMeetings();
  }

  loadMeetings(): void {
    this.loading = true;
    this.error = null;

    forkJoin({
      meetingsData: this.meetingsService.getAll(),
      projectsData: this.projectsService.getAll()
    }).subscribe({
      next: ({ meetingsData, projectsData }) => {
        

        this.projectNamesMap = new Map(
          projectsData.map(p => [p.id!, p.name])
        );

        this.meetings = meetingsData.sort(
          (a, b) =>
            new Date(a.meetingDate!).getTime() -
            new Date(b.meetingDate!).getTime()
        );
        
        this.loading = false;
      },
      error: (err) => {
        console.error(err);
        this.error = 'Failed to load meetings or project data.';
        this.loading = false;
      }
    });
  }

  isPast(date?: string): boolean {
    if (!date) return false;
    return new Date(date) < new Date();
  }

  formatDate(date?: string): string {
    return date ? new Date(date).toLocaleString() : '-';
  }

  getProjectName(projectId: number): string {
    return this.projectNamesMap.get(projectId) || `Unknown Project ID: ${projectId}`;
  }
}