package com.example.demo.controller;

import com.example.demo.service.AIChatService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/chatAI")
public class ChatAIController {
    private AIChatService aiChatService;

    public ChatAIController(AIChatService aiChatService) {
        this.aiChatService = aiChatService;
    }
    @GetMapping
    public String getResponse(@RequestParam String prompt) {
        return aiChatService.getResponse(prompt);
    }
}
