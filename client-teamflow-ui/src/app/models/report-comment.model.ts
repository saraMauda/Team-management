// src/app/models/report-comment.model.ts

export interface ReportComment {
  commentId: number;
  // ⭐ שדות אלו מגיעים מה-Getters ב-Java (getUserId, getAuthorRole) ⭐
  userId: number;       
  authorRole: string;   
  // -----------------------------------------------------------------
  reportId?: number; 
  text: string;
  isEdited: boolean;
  commentDate: string; // ISO datetime
}