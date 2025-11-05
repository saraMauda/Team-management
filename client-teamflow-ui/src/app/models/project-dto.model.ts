// src/app/models/project-dto.model.ts

export interface ProjectDTO {
  id?: number;                 // מגיע מהשרת
  name: string;                // שם הפרויקט
  description?: string;        // תיאור
  startDate?: string | null;   // 'YYYY-MM-DD'
  endDate?: string | null;     // 'YYYY-MM-DD'
  status?: string | null;      // ACTIVE / COMPLETED / ...
  progress?: number | null;    // 0-100
  leaderName?: string | null;
  categoryName?: string | null;
}
