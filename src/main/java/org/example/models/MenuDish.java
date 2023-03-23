package org.example.models;
import com.fasterxml.jackson.annotation.JsonProperty;

public class MenuDish {
    @JsonProperty("menu_dish_id")
    private int menuDishId;

    @JsonProperty("menu_dish_card")
    private int menuDishCard;

    @JsonProperty("menu_dish_price")
    private int menuDishPrice;

    @JsonProperty("menu_dish_active")
    private boolean menuDishActive;

    public int getMenuDishId() {
        return menuDishId;
    }

    public void setMenuDishId(int menuDishId) {
        this.menuDishId = menuDishId;
    }

    public int getMenuDishCard() {
        return menuDishCard;
    }

    public void setMenuDishCard(int menuDishCard) {
        this.menuDishCard = menuDishCard;
    }

    public int getMenuDishPrice() {
        return menuDishPrice;
    }

    public void setMenuDishPrice(int menuDishPrice) {
        this.menuDishPrice = menuDishPrice;
    }

    public boolean isMenuDishActive() {
        return menuDishActive;
    }

    public void setMenuDishActive(boolean menuDishActive) {
        this.menuDishActive = menuDishActive;
    }
}
