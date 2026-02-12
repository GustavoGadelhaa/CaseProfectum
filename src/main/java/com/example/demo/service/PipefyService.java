package com.example.demo.service;

import com.example.demo.client.GraphQLRequest;
import com.example.demo.dtos.response.CardResponse;
import com.example.demo.dtos.response.PipefyResponse;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class PipefyService {

    private final String API_URL = "https://api.pipefy.com/graphql";
    private final String TOKEN = "Bearer eyJhbGciOiJIUzUxMiJ9.eyJpc3MiOiJQaXBlZnkiLCJpYXQiOjE3MDQyMDIzOTMsImp0aSI6IjAyZmI0MGFmLWYwNGQtNGNjMi05Yjc4LWJkZmQ5YzhhZWM4NCIsInN1YiI6MzA0MTY1MTY2LCJ1c2VyIjp7ImlkIjozMDQxNjUxNjYsImVtYWlsIjoiZGVzYWZpb2ludGVncmFjYW9AcHJvZmVjdHVtLmNvbS5iciIsImFwcGxpY2F0aW9uIjozMDAzMDU3MDEsInNjb3BlcyI6W119LCJpbnRlcmZhY2VfdXVpZCI6bnVsbH0.NDCy-EvEyaQpct5lEeaXRdCCWCuU4K-DRggf2wdZIsVMo8tIwk0kY7bPVPnngajjULE_hF-O0rqqydkyzJiNBA";
    private final String FASE_FIM_ID = "323403004";

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


        if (FASE_FIM_ID.equals(card.getCurrentPhase().getId())) {
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
        headers.set("Authorization", TOKEN);

        HttpEntity<GraphQLRequest> entity = new HttpEntity<>(new GraphQLRequest(query), headers);
        return restTemplate.postForObject(API_URL, entity, PipefyResponse.class);
    }
}