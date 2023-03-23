package org.example.models;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;


public class DishCard {
    @JsonProperty("card_id")
    private int cardId;

    @JsonProperty("dish_name")
    private String dishName;

    @JsonProperty("card_descr")
    private String cardDescr;

    @JsonProperty("card_time")
    private double cardTime;

    @JsonProperty("equip_type")
    private int equipType;

    @JsonProperty("operations")
    private List<Operation> operations;

    public int getCardId() {
        return cardId;
    }

    public void setCardId(int cardId) {
        this.cardId = cardId;
    }

    public String getDishName() {
        return dishName;
    }

    public void setDishName(String dishName) {
        this.dishName = dishName;
    }

    public String getCardDescr() {
        return cardDescr;
    }

    public void setCardDescr(String cardDescr) {
        this.cardDescr = cardDescr;
    }

    public double getCardTime() {
        return cardTime;
    }

    public void setCardTime(double cardTime) {
        this.cardTime = cardTime;
    }

    public int getEquipType() {
        return equipType;
    }

    public void setEquipType(int equipType) {
        this.equipType = equipType;
    }

    public List<Operation> getOperations() {
        return operations;
    }

    public void setOperations(List<Operation> operations) {
        this.operations = operations;
    }
}
