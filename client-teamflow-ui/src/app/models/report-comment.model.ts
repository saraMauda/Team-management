// src/app/models/report-comment.model.ts
export interface ReportComment {
  commentId: number;
  reportId: number;
  userId: number;
  text: string;
  isEdited: boolean;
  commentDate?: string; // ISO datetime
}
