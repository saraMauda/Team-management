// src/app/models/report-create-dto.model.ts
export interface ReportCreateDTO {
  projectId: number;
  title: string;
  description: string;
  status?: string;
}
