package org.example.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.List;

public class Menu {
    @JsonProperty("menu_dishes")
    private List<MenuDish> menuDishes;

    public List<MenuDish> getMenuDishes() {
        return menuDishes;
    }

    public void setMenuDishes(List<MenuDish> menuDishes) {
        this.menuDishes = menuDishes;
    }

    public String toJsonString() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(this);
    }

    public static Menu fromJsonString(String jsonString) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(jsonString, Menu.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
