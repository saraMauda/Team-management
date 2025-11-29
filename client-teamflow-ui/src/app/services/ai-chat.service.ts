import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { API_BASE_URL } from '../app.config';

@Injectable({
  providedIn: 'root'
})
export class AiChatService {

  private baseUrl = `${API_BASE_URL}/chatAI`;

  constructor(private http: HttpClient) {}

  /** ✔ שולח שאלה ל-AI ומחזיר טקסט */
sendMessage(message: string, conversationId: string): Observable<string> {
  const body = { message, conversationId };
  return this.http.post(this.baseUrl, body, {
    responseType: 'text',
    withCredentials: true
  });
}

}
