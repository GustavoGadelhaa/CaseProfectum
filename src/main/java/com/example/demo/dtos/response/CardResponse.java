package com.example.demo.dtos.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class CardResponse {
    private String id;
    @JsonProperty("current_phase")
    private PhaseResponse currentPhase;
}