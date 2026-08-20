package com.example.demo.service;

import com.example.demo.client.GraphQLRequest;
import com.example.demo.dtos.response.CardResponse;
import com.example.demo.dtos.response.PipefyResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class PipefyService {

    @Value("${pipefy.api-url}")
    private String apiUrl;

    @Value("${pipefy.token}")
    private String token;

    @Value("${pipefy.fase-fim-id}")
    private String faseFimId;

    private final RestTemplate restTemplate = new RestTemplate();

    public Object processarDelecao(String cardId) {
        PipefyResponse response = deleteCard(cardId);

        if (response.getData() != null && response.getData().getDeleteCard().isSuccess()) {
            return Map.of("message", "Card " + cardId + " deletado com sucesso");
        }
        return response.getErrors() != null ? response.getErrors() : Map.of("error", "Falha ao deletar");
    }

    public Object processarMovimentacao(String cardId, String phaseId) {
        PipefyResponse response = moveCard(cardId, phaseId);

        if (response.getErrors() != null && !response.getErrors().isEmpty()) {
            return response.getErrors();
        }

        CardResponse card = response.getData().getMoveCard().getCard();


        if (faseFimId.equals(card.getCurrentPhase().getId())) {
            Map<String, Object> finalResult = new HashMap<>();
            finalResult.put("status", "PROCESSO_FINALIZADO");
            finalResult.put("mensagem", "O card atingiu a fase final: " + card.getCurrentPhase().getName());
            finalResult.put("cardId", card.getId());
            return finalResult;
        }

        return card;
    }

    private PipefyResponse deleteCard(String cardId) {
        String query = String.format("mutation { deleteCard(input: { id: \"%s\" }) { success } }", cardId);
        return execute(query);
    }

    private PipefyResponse moveCard(String cardId, String phaseId) {
        String query = String.format(
                "mutation { moveCardToPhase(input: { card_id: \"%s\", destination_phase_id: \"%s\" }) { card { id current_phase { id name } } } }",
                cardId, phaseId
        );
        return execute(query);
    }

    private PipefyResponse execute(String query) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", token);

        HttpEntity<GraphQLRequest> entity = new HttpEntity<>(new GraphQLRequest(query), headers);
        return restTemplate.postForObject(apiUrl, entity, PipefyResponse.class);
    }
}