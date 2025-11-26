export interface MeetingDTO {
  meetingId: number;
  title: string;
  meetingDate?: string;
  description?: string;
  meetingLocation?: string;
  status?: string | null;
  createdAt?: string;
  projectId: number;
  progress?: number | null;
}
