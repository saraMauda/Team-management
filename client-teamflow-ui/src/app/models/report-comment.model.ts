// src/app/models/report-comment.model.ts

export interface ReportComment {
  commentId: number;
  userId: number;       
  authorRole: string;   
  reportId?: number; 
  text: string;
  isEdited: boolean;
  commentDate: string; 
}