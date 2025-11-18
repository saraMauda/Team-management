import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MeetingsService } from '../../../../../services/meetings.service';
import { MeetingDTO } from '../../../../../models/meeting-dto.model';

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

  constructor(private meetingsService: MeetingsService) {}

  ngOnInit(): void {
    this.loadMeetings();
  }

  loadMeetings(): void {
    this.meetingsService.getAll().subscribe({
      next: (data: MeetingDTO[]) => {
        this.meetings = data.sort(
          (a, b) =>
            new Date(a.meetingDate!).getTime() -
            new Date(b.meetingDate!).getTime()
        );
        this.loading = false;
      },
      error: () => {
        this.error = 'Failed to load meetings.';
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
}
