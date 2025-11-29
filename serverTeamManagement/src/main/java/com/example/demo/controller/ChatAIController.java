package com.example.demo.controller;

import com.example.demo.dto.ChatRequest;
import com.example.demo.service.AIChatService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

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
    @PostMapping
    public String chat(@RequestBody ChatRequest request, Authentication authentication) {
        return aiChatService.getResponse(
                request.message(),
                request.conversationId(),
                authentication
        );
    }

}
