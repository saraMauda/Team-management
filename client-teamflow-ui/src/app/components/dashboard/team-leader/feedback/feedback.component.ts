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
    this.loadFeedbacks();
  }

  loadFeedbacks(): void {
    this.reportsService.getAll().subscribe({
      next: (data) => {
        // כאן את יכולה לשנות לסינון רק פידבקים לצוות של ראש הצוות
        this.feedbacks = data
          .filter((f: any) => f.type === 'FEEDBACK' || f.feedback)
          .reverse(); // רק כדי להציג מהחדש לישן
        this.loading = false;
      },
      error: (err) => {
        console.error('❌ Failed to load feedbacks', err);
        this.error = 'Failed to load feedbacks.';
        this.loading = false;
      }
    });
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
