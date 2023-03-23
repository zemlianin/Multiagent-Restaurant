package org.example.models;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

public class Operation {
    @JsonProperty("oper_type")
    private int operType;

    @JsonProperty("oper_time")
    private double operTime;

    @JsonProperty("oper_async_point")
    private int operAsyncPoint;

    @JsonProperty("oper_products")
    private List<Product> operProducts;

    public int getOperType() {
        return operType;
    }

    public void setOperType(int operType) {
        this.operType = operType;
    }

    public double getOperTime() {
        return operTime;
    }

    public void setOperTime(double operTime) {
        this.operTime = operTime;
    }

    public int getOperAsyncPoint() {
        return operAsyncPoint;
    }

    public void setOperAsyncPoint(int operAsyncPoint) {
        this.operAsyncPoint = operAsyncPoint;
    }

    public List<Product> getOperProducts() {
        return operProducts;
    }

    public void setOperProducts(List<Product> operProducts) {
        this.operProducts = operProducts;
    }
}
