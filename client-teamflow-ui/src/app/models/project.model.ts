export interface Project {
  id?: number;
  name: string;
  description?: string;

  startDate?: string | null;
  endDate?: string | null;

  status?: string | null;
  progress?: number | null;

  leaderId?: number | null;
  categoryId?: number | null;

  location?: string | null;
  employeeIds?: number[];
}
