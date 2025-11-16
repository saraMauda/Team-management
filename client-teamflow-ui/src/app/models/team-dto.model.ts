import { UsersDTO } from './users-dto.model';

export interface TeamDTO {
  id: number;
  leaderId: number;
  leaderName: string;
  members: UsersDTO[];
}
