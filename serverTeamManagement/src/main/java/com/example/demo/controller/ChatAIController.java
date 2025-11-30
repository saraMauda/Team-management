package com.example.demo.controller;

import com.example.demo.dto.ChatRequest;
import com.example.demo.service.AIChatService;
import javax.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/chatAI")
public class ChatAIController {

    private final AIChatService aiChatService;

    public ChatAIController(AIChatService aiChatService) {
        this.aiChatService = aiChatService;
    }

    @PostMapping
    @PreAuthorize("hasRole('TEAMLEADER')")
    public String chat(@Valid @RequestBody ChatRequest request, Authentication authentication) {
        if (authentication == null) {
            throw new RuntimeException("User is not authenticated");
        }
        return aiChatService.getResponse(
                request.message(),
                request.conversationId(),
                authentication
        );
    }
}
