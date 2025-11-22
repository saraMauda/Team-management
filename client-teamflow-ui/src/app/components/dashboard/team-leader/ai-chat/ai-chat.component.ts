import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AiChatService } from '../../../../services/ai-chat.service';

interface ChatMessage {
  sender: 'user' | 'bot';
  text: string;
}

@Component({
  selector: 'app-ai-chat',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './ai-chat.component.html',
  styleUrls: ['./ai-chat.component.css']
})
export class AiChatComponent {

  userInput = '';
  messages: ChatMessage[] = [];
  loading = false;

  constructor(private aiService: AiChatService) {}

  sendMessage() {
    if (!this.userInput.trim()) return;

    this.messages.push({
      sender: 'user',
      text: this.userInput
    });

    const prompt = this.userInput;
    this.userInput = '';
    this.loading = true;

    this.aiService.sendMessage(prompt).subscribe({
      next: (response) => {
        this.messages.push({ sender: 'bot', text: response });
        this.loading = false;
        setTimeout(() => this.scrollToBottom(), 50);
      },
      error: () => {
        this.messages.push({
          sender: 'bot',
          text: 'An error occurred. Please try again.'
        });
        this.loading = false;
      }
    });
  }

  private scrollToBottom() {
    const box = document.getElementById('chatBox');
    if (box) {
      box.scrollTop = box.scrollHeight;
    }
  }
}
