export interface ReportDTO {
  id: number;
  title: string;
  description: string;
  status: string;
  reportDate: string;
  lastEdited: string;
  
  employeeName: string;
  projectName: string;
  projectId: number;
  userId: number;       
  
  commentCount: number;
}
