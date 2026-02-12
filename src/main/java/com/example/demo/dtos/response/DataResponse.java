package com.example.demo.dtos.response;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class DataResponse {
    @JsonProperty("moveCardToPhase")
    private MoveCardWrapper moveCard;

    @JsonProperty("deleteCard")
    private DeleteResponse deleteCard;

    @Data
    public static class MoveCardWrapper {
        private CardResponse card;
    }
}