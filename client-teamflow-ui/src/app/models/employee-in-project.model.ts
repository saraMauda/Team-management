// src/app/models/employee-in-project.model.ts
export interface EmployeeInProject {
  employeeProjectId: number;
  projectId: number;
  userId: number;
  roleDescription?: string;
  status?: string;
  assignedDate?: string; // ISO date string (yyyy-MM-dd)
}
