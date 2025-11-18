import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ReportsService } from '../../../../services/reports.service'; // אם יש לך שירות פידבק נפרד - תחליפי
// לדוגמה FeedbackService, אבל כרגע אפשר להשתמש ב־reportsService כדמה.

@Component({
  selector: 'app-feedback',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './feedback.component.html',
  styleUrls: ['./feedback.component.css']
})
export class FeedbackComponent implements OnInit {
  feedbacks: any[] = [];
  newFeedback: string = '';
  loading = true;
  error: string | null = null;

  constructor(private reportsService: ReportsService) {}

  ngOnInit(): void {
  }
  sendFeedback(): void {
    if (!this.newFeedback.trim()) return;

    const newEntry = {
      sender: 'Team Leader',
      content: this.newFeedback,
      date: new Date().toISOString()
    };

    // 🔹 במערכת אמיתית תשתמשי בשירות פידבק ל־POST
    this.feedbacks.unshift(newEntry);
    this.newFeedback = '';
  }
}
