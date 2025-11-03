// src/app/models/approval.model.ts
export interface Approval {
  approvalId: number;
  meetingId: number;
  employeeInProjectId: number;
  approved: boolean;
  approvalDate?: string; // yyyy-MM-dd
}
