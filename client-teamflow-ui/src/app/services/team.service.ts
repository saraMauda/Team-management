import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { API_BASE_URL } from '../app.config';
import { TeamDTO } from '../models/team-dto.model';

@Injectable({
  providedIn: 'root'
})
export class TeamService {

  private baseUrl = `${API_BASE_URL}/teams`;

  constructor(private http: HttpClient) {}

  getAllTeams(): Observable<TeamDTO[]> {
    return this.http.get<TeamDTO[]>(`${this.baseUrl}/all`, {
      withCredentials: true
    });
  }

  createTeam(leaderId: number, memberIds: number[]): Observable<TeamDTO> {
    return this.http.post<TeamDTO>(`${this.baseUrl}/create/${leaderId}`, memberIds, {
      withCredentials: true
    });
  }

  getTeam(id: number): Observable<TeamDTO> {
    return this.http.get<TeamDTO>(`${this.baseUrl}/${id}`, {
      withCredentials: true
    });
  }

  addMember(teamId: number, userId: number): Observable<TeamDTO> {
    return this.http.post<TeamDTO>(`${this.baseUrl}/${teamId}/add/${userId}`, {}, {
      withCredentials: true
    });
  }

  removeMember(memberId: number): Observable<TeamDTO> {
    return this.http.delete<TeamDTO>(`${this.baseUrl}/remove/${memberId}`, {
      withCredentials: true
    });
  }

getTeamsByLeader(leaderId: number): Observable<TeamDTO[]> {
  return this.http.get<TeamDTO[]>(`${this.baseUrl}/byLeader/${leaderId}`, {
    withCredentials: true
  });
}

}
