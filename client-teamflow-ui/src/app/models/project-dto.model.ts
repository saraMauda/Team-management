export interface ProjectDTO {
  id?: number;
  name: string;
  description?: string;

  startDate?: string | null;
  endDate?: string | null;

  status?: string | null;
  progress?: number | null;

  leaderName?: string | null;
  leaderId?: number | null;

  categoryName?: string | null;
  categoryId?: number | null;

  location?: string | null;

  employeeIds?: number[];
}