package br.com.alura.ecomart.chatbot.infra.openai;


import com.theokanning.openai.completion.chat.ChatMessageRole;
import com.theokanning.openai.messages.Message;
import com.theokanning.openai.messages.MessageRequest;
import com.theokanning.openai.runs.RunCreateRequest;
import com.theokanning.openai.service.OpenAiService;
import com.theokanning.openai.threads.ThreadRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Arrays;
import java.util.Comparator;

@Component
public class OpenAIClient {

    private final String apiKey;
    private final String assistantId;
    private final OpenAiService service;
    private String threadId;

    public OpenAIClient(@Value("${app.openai.api.key}") String apiKey,
                        @Value("${app.openai.assistant.id}") String assistantId) {

        this.apiKey = apiKey;
        this.service = new OpenAiService(apiKey, Duration.ofSeconds(60));
        this.assistantId = assistantId;
    }

    public String enviarRequisicaoChatCompletion(DadosRequisicaoChatCompletion dados) {

        // Create the message
        var messageRequest = MessageRequest
                .builder()
                .role(ChatMessageRole.USER.value())
                .content(dados.promptUsuario())
                .build();

        // Create the Thread
        if (this.threadId == null) {
            var threadRequest = ThreadRequest
                    .builder()
                    .messages(Arrays.asList(messageRequest))
                    .build();

            var thread = service.createThread(threadRequest);
            this.threadId = thread.getId();
        } else {
            service.createMessage(this.threadId, messageRequest);
        }

        // Create the Run
        var runRequest = RunCreateRequest
                .builder()
                .assistantId(assistantId)
                .build();

        var run = service.createRun(threadId, runRequest);

        // Check if the Run is finalized
        try {
            while (!run.getStatus().equalsIgnoreCase("completed")) {
                Thread.sleep(1000 * 10);
                run = service.retrieveRun(threadId, run.getId());
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }


        var mensagens = service.listMessages(threadId);
        var respostaAssistente = mensagens
                .getData()
                .stream()
                .sorted(Comparator.comparingInt(Message::getCreatedAt).reversed())
                .findFirst().get().getContent().get(0).getText().getValue();

        return respostaAssistente;
    }

}
