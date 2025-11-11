import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MeetingsService } from '../../../../services/meetings.service';

@Component({
  selector: 'app-meetings',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './meetings.component.html',
  styleUrls: ['./meetings.component.css']
})
export class MeetingsComponent implements OnInit {
  meetings: any[] = [];
  loading = true;
  error: string | null = null;

  constructor(private meetingsService: MeetingsService) {}

  ngOnInit(): void {
    this.loadMeetings();
  }

  loadMeetings(): void {
    this.meetingsService.getMyMeetings().subscribe({
      next: (data:any) => {
        this.meetings = data.sort(
          (a: any, b: any) => new Date(a.date).getTime() - new Date(b.date).getTime()
        );
        this.loading = false;
      },
      error: (err: any) => {
        console.error('❌ Failed to load meetings', err);
        this.error = 'Failed to load your meetings.';
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
