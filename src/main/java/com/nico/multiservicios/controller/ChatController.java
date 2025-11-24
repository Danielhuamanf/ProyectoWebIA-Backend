package com.nico.multiservicios.controller;

import com.nico.multiservicios.dto.ChatRequest;
import com.nico.multiservicios.dto.ChatResponse;
import com.nico.multiservicios.service.GeminiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(ChatController.class);

    @Autowired
    private GeminiService geminiService;

    @PostMapping
    public ResponseEntity<ChatResponse> chat(@RequestBody ChatRequest request) {
        logger.info("Recibida solicitud de chat: {}", request.getMessage());
        String responseMessage = geminiService.getChatResponse(request.getMessage());
        logger.info("Respuesta generada: {}", responseMessage);
        return ResponseEntity.ok(new ChatResponse(responseMessage));
    }
}
