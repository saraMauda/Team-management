package com.example.demo.controller;

import com.example.demo.service.AIChatService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/chatAI")
public class ChatAIController {

    private final AIChatService aiChatService;

    public ChatAIController(AIChatService aiChatService) {
        this.aiChatService = aiChatService;
    }

    /**
     * Chat endpoint for the Team Leader assistant.
     * Uses the authenticated user (from JWT cookie) to understand who is asking.
     */
    @GetMapping
    public String getResponse(@RequestParam String prompt, Authentication authentication) {
        return aiChatService.getResponse(prompt, authentication);
    }
}
