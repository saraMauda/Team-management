// src/app/models/project-dto.model.ts
import { Category } from './category.model';

export interface ProjectDTO {
  projectId: number;
  projectName: string;
  projectDescription?: string;
  projectLocation?: string;
  projectStatus?: string;          // Active / Completed / On Hold...
  projectStartDate?: string;       // yyyy-MM-dd
  projectEndDate?: string;         // yyyy-MM-dd
  lastUpdated?: string;            // yyyy-MM-dd
  progressPercentage: number;

  // ב־DB יש category_id ו user_id – ב־DTO לרוב מוסיפים:
  categoryId?: number;
  category?: Category;
  projectLeaderId?: number;
  projectLeaderName?: string;
}
