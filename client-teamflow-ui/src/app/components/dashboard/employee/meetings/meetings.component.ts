import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MeetingsService } from '../../../../services/meetings.service';
import { MeetingDTO } from '../../../../models/meeting-dto.model';
import { ProjectsService } from '../../../../services/projects.service';

@Component({
  selector: 'app-meetings',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './meetings.component.html',
  styleUrls: ['./meetings.component.css']
})
export class MeetingsComponent implements OnInit {

  // שיניתי ל-any כדי לאפשר field חדש projectName
  meetings: any[] = [];
  loading = true;
  error: string | null = null;

  constructor(
    private meetingsService: MeetingsService,
    private projectsService: ProjectsService
  ) {}

  ngOnInit(): void {
    this.loadMeetings();
  }

  /** ✨ טוען פגישות + מוסיף projectName מכל הפרויקטים */
  loadMeetings(): void {
    this.meetingsService.getMyMeetings().subscribe({
      next: (meetings: MeetingDTO[]) => {

        // אחרי שיש לנו פגישות – נטען את הפרויקטים
        this.projectsService.getAll().subscribe({
          next: (projects: any[]) => {

            const enriched = meetings.map(m => {
              // מניח שיש m.projectId שמגיע מהשרת
              const projectId = (m as any).projectId;

              const project = projects.find(p =>
                p.id === projectId || p.projectId === projectId
              );

              return {
                ...m,
                projectName: project ? (project.name || project.projectName || 'Unnamed project') : null
              };
            });

            this.meetings = enriched.sort(
              (a: any, b: any) =>
                new Date(a.meetingDate!).getTime() - new Date(b.meetingDate!).getTime()
            );
            this.loading = false;
          },
          error: () => {
            // במקרה שהפרויקטים נופלים – עדיין נציג פגישות בלי projectName
            this.meetings = meetings.sort(
              (a: MeetingDTO, b: MeetingDTO) =>
                new Date(a.meetingDate!).getTime() - new Date(b.meetingDate!).getTime()
            );
            this.loading = false;
          }
        });
      },
      error: () => {
        this.error = 'Failed to load your meetings.';
        this.loading = false;
      }
    });
  }

  isPast(date: string | undefined): boolean {
    if (!date) return false;
    return new Date(date) < new Date();
  }

  formatDate(date: string | undefined): string {
    return date ? new Date(date).toLocaleString() : '-';
  }
}
