package org.example.models;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.List;

public class DishCards {
    @JsonProperty("dish_cards")
    private List<DishCard> cards;

    public List<DishCard> getCards() {
        return cards;
    }

    public void setCards(List<DishCard> cards) {
        this.cards = cards;
    }

    public String toJsonString() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(this);
    }

    public static DishCard fromJsonString(String jsonString) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(jsonString, DishCard.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
