export interface Project {
  id?: number;
  name: string;
  description?: string;

  startDate?: string | null;
  endDate?: string | null;

  status?: string | null;
  progressPercentage?: number | null;

  leaderId?: number | null;
  leaderName?: string | null;

  location?: string | null;

  employeeIds?: number[];
}
