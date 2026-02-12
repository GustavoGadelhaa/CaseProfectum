package com.example.demo.service;


import com.example.demo.client.GraphQLRequest;
import com.example.demo.dtos.response.PipefyResponse;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class PipefyService {

    private final String API_URL = "https://api.pipefy.com/graphql";
    private final String TOKEN = "Bearer eyJhbGciOiJIUzUxMiJ9.eyJpc3MiOiJQaXBlZnkiLCJpYXQiOjE3MDQyMDIzOTMsImp0aSI6IjAyZmI0MGFmLWYwNGQtNGNjMi05Yjc4LWJkZmQ5YzhhZWM4NCIsInN1YiI6MzA0MTY1MTY2LCJ1c2VyIjp7ImlkIjozMDQxNjUxNjYsImVtYWlsIjoiZGVzYWZpb2ludGVncmFjYW9AcHJvZmVjdHVtLmNvbS5iciIsImFwcGxpY2F0aW9uIjozMDAzMDU3MDEsInNjb3BlcyI6W119LCJpbnRlcmZhY2VfdXVpZCI6bnVsbH0.NDCy-EvEyaQpct5lEeaXRdCCWCuU4K-DRggf2wdZIsVMo8tIwk0kY7bPVPnngajjULE_hF-O0rqqydkyzJiNBA";

    private final RestTemplate restTemplate = new RestTemplate();

    private HttpHeaders createHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", TOKEN);
        return headers;
    }

    public PipefyResponse deleteCard(String cardId) {
        String query = String.format("mutation { deleteCard(input: { id: \"%s\" }) { success } }", cardId);
        return execute(query);
    }

    public PipefyResponse moveCard(String cardId, String phaseId) {
        String query = String.format(
                "mutation { moveCardToPhase(input: { card_id: \"%s\", destination_phase_id: \"%s\" }) { card { id current_phase { id name } } } }",
                cardId, phaseId
        );
        return execute(query);
    }

    private PipefyResponse execute(String query) {
        HttpEntity<GraphQLRequest> entity = new HttpEntity<>(new GraphQLRequest(query), createHeaders());
        return restTemplate.postForObject(API_URL, entity, PipefyResponse.class);
    }
}