import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';

import { MeetingsService } from '../../../../../services/meetings.service';
import { ProjectsService } from '../../../../../services/projects.service'; // ייבוא חדש
import { MeetingDTO } from '../../../../../models/meeting-dto.model';
import { ProjectDTO } from '../../../../../models/project-dto.model'; // ייבוא חדש

import { forkJoin } from 'rxjs'; // ייבוא חדש לביצוע קריאות מקבילות

@Component({
  selector: 'app-manage-meetings',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './manage-meetings.component.html',
  styleUrls: ['./manage-meetings.component.css']
})
export class ManageMeetingsComponent implements OnInit {

  meetings: MeetingDTO[] = [];
  loading = true;
  error: string | null = null;
  
  // מפה חדשה לאחסון שמות הפרויקטים לפי ID
  projectNamesMap: Map<number, string> = new Map(); 

  constructor(
    private meetingsService: MeetingsService,
    private projectsService: ProjectsService // הזרקת ProjectsService
  ) {}

  ngOnInit(): void {
    this.loadMeetings();
  }

  loadMeetings(): void {
    this.loading = true;
    this.error = null;

    // מריצים שתי קריאות במקביל: פגישות ופרויקטים
    forkJoin({
      meetingsData: this.meetingsService.getAll(),
      projectsData: this.projectsService.getAll()
    }).subscribe({
      next: ({ meetingsData, projectsData }) => {
        
        // 1. בונים מפה מהירה של ID -> Name
        this.projectNamesMap = new Map(
          projectsData.map(p => [p.id!, p.name])
        );

        // 2. מסדרים את הפגישות
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

  // פונקציה חדשה: מחזירה את שם הפרויקט לפי ID
  getProjectName(projectId: number): string {
    return this.projectNamesMap.get(projectId) || `Unknown Project ID: ${projectId}`;
  }
}