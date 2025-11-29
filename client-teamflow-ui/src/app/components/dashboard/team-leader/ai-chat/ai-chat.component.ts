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
  conversationId = crypto.randomUUID();

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

  // ✔ שולחים גם conversationId
  this.aiService.sendMessage(prompt, this.conversationId).subscribe({
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


private scrollToBottom(): void {
  setTimeout(() => {
    const box = document.getElementById('chatBox');
    if (box) {
      box.scrollTo({
        top: box.scrollHeight,
        behavior: 'smooth'
      });
    }
  }, 70);
}

}
