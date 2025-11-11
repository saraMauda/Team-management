import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MeetingsService } from '../../../../services/meetings.service';

@Component({
  selector: 'app-team-meetings',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './team-meetings.component.html',
  styleUrls: ['./team-meetings.component.css']
})
export class TeamMeetingsComponent implements OnInit {
  meetings: any[] = [];
  loading = true;
  error: string | null = null;

  constructor(private meetingsService: MeetingsService) {}

  ngOnInit(): void {
    this.loadMeetings();
  }

  loadMeetings(): void {
    this.meetingsService.getAll().subscribe({
      next: (data) => {
        // כאן אפשר לסנן ישיבות לפי ראש הצוות, אם יש שדה מתאים ב־API
        this.meetings = data.sort(
          (a: any, b: any) => new Date(b.date).getTime() - new Date(a.date).getTime()
        );
        this.loading = false;
      },
      error: (err) => {
        console.error('❌ Failed to load meetings', err);
        this.error = 'Failed to load meetings.';
        this.loading = false;
      }
    });
  }

  isPast(date: string): boolean {
    return new Date(date) < new Date();
  }

  formatDate(date: string): string {
    return new Date(date).toLocaleString();
  }
}
