package com.example.demo.controller;

import com.example.demo.dtos.request.MoveCardRequest;
import com.example.demo.service.PipefyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/pipefy")
public class PipefyController {

    @Autowired
    private PipefyService pipefyService;

    @DeleteMapping("/card/{id}")
    public ResponseEntity<?> deletarCard(@PathVariable String id) {
        Object response = pipefyService.processarDelecao(id);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/card/{id}/etapa")
    public ResponseEntity<?> moverCard(@PathVariable String id, @RequestBody MoveCardRequest request) {
        Object response = pipefyService.processarMovimentacao(id, request.getDestinationPhaseId());
        return ResponseEntity.ok(response);
    }
}