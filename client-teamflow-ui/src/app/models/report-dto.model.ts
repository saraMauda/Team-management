export interface ReportDTO {
  id: number;
  employeeProjectId: number;
  
  title: string;
  description?: string;
  status?: string;
  
  date?: string;
  lastEdited?: string;
}
