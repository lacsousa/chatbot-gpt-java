package br.com.alura.ecomart.chatbot.web.controller;

import br.com.alura.ecomart.chatbot.domain.service.ChatbotService;
import br.com.alura.ecomart.chatbot.web.dto.PerguntaDto;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping({"/", "chat"})
public class ChatController {

    private static final String PAGINA_CHAT = "chat";

    private ChatbotService chatbotService;

    public ChatController(ChatbotService chatbotService) {
        this.chatbotService = chatbotService;
    }

    @GetMapping
    public String carregarPaginaChatbot() {
        return PAGINA_CHAT;
    }

    @PostMapping
    @ResponseBody
    public String responderPergunta(@RequestBody PerguntaDto dto) {
        return chatbotService.responderPergunta(dto.pergunta());
    }

    @GetMapping("limpar")
    public String limparConversa() {
        return PAGINA_CHAT;
    }

}
