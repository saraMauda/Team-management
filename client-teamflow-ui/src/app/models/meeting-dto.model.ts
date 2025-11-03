// src/app/models/meeting-dto.model.ts
export interface MeetingDTO {
  meetingId: number;
  projectId: number;
  title: string;
  description?: string;
  meetingLocation?: string;
  status?: string;        // Scheduled / Done / Canceled...
  meetingDate?: string;   // yyyy-MM-dd
  createdAt?: string;     // ISO datetime
}
