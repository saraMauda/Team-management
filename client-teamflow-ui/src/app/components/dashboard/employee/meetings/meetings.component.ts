import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MeetingsService } from '../../../../services/meetings.service';
import { MeetingDTO } from '../../../../models/meeting-dto.model';

@Component({
  selector: 'app-meetings',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './meetings.component.html',
  styleUrls: ['./meetings.component.css']
})
export class MeetingsComponent implements OnInit {
  meetings: MeetingDTO[] = [];
  loading = true;
  error: string | null = null;

  constructor(private meetingsService: MeetingsService) {}

  ngOnInit(): void {
    this.loadMeetings();
  }

  loadMeetings(): void {
    this.meetingsService.getMyMeetings().subscribe({
      next: (data: MeetingDTO[]) => {
        this.meetings = data.sort(
          (a: MeetingDTO, b: MeetingDTO) =>
            new Date(a.meetingDate!).getTime() - new Date(b.meetingDate!).getTime()
        );
        this.loading = false;
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
