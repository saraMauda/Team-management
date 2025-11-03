// src/app/models/report-dto.model.ts
export interface ReportDTO {
  reportId: number;
  employeeProjectId: number;   // קישור ל-EmployeeInProject
  reportTitle: string;
  reportDescription?: string;
  reportStatus?: string;
  reportDate?: string;         // yyyy-MM-dd
  lastEdited?: string;         // yyyy-MM-dd
}
