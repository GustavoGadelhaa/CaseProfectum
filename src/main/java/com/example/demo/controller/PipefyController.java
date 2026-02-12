package com.example.demo.controller;

import com.example.demo.dtos.request.MoveCardRequest;
import com.example.demo.dtos.response.CardResponse;
import com.example.demo.dtos.response.PipefyResponse;
import com.example.demo.service.PipefyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/pipefy")
public class PipefyController {

    @Autowired
    private PipefyService pipefyService;

    // ID da fase "Concluído" mapeado anteriormente
    private final String FASE_FIM_ID = "323403004";

    @DeleteMapping("/card/{id}")
    public ResponseEntity<?> deletarCard(@PathVariable String id) {
        PipefyResponse response = pipefyService.deleteCard(id);

        if (response.getData() != null && response.getData().getDeleteCard().isSuccess()) {
            return ResponseEntity.ok().body(Map.of("message", "Card " + id + " deletado com sucesso"));
        }
        return ResponseEntity.badRequest().body(response.getErrors());
    }

    @PatchMapping("/card/{id}/avancar")
    public ResponseEntity<?> moverCard(@PathVariable String id, @RequestBody MoveCardRequest request) {
        PipefyResponse response = pipefyService.moveCard(id, request.getDestinationPhaseId());

        if (response.getErrors() != null && !response.getErrors().isEmpty()) {
            return ResponseEntity.badRequest().body(response.getErrors());
        }

        CardResponse card = response.getData().getMoveCard().getCard();

        // Lógica solicitada: Se chegar na fase fim, retorna informação específica
        if (FASE_FIM_ID.equals(card.getCurrentPhase().getId())) {
            Map<String, Object> finalResult = new HashMap<>();
            finalResult.put("status", "PROCESSO_FINALIZADO");
            finalResult.put("mensagem", "O card atingiu a fase final: " + card.getCurrentPhase().getName());
            finalResult.put("cardId", card.getId());
            return ResponseEntity.ok(finalResult);
        }

        return ResponseEntity.ok(card);
    }
}