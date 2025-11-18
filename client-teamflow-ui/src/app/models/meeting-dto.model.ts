export interface MeetingDTO {
  meetingId: number;
  title: string;
  meetingDate?: string;
  description?: string;
  meetingLocation?: string;
  status?: string;
  createdAt?: string;
  projectId: number;
}
