package com.worth.ifs.finance.resource;

import com.worth.ifs.finance.domain.CostValue;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class CostResource {
    private Long id;
    private String item;
    private String description;
    private Integer quantity;
    private BigDecimal cost;
    private List<CostValue> costValues = new ArrayList<>();
    private Long applicationFinance;
    private Long question;

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getItem() {
        return this.item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getQuantity() {
        return this.quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getCost() {
        return this.cost;
    }

    public void setCost(BigDecimal cost) {
        this.cost = cost;
    }

    public List<CostValue> getCostValues() {
        return this.costValues;
    }

    public void setCostValues(List<CostValue> costValues) {
        this.costValues = costValues;
    }

    public Long getApplicationFinance() {
        return this.applicationFinance;
    }

    public void setApplicationFinance(Long applicationFinance) {
        this.applicationFinance = applicationFinance;
    }

    public Long getQuestion() {
        return this.question;
    }

    public void setQuestion(Long question) {
        this.question = question;
    }
}
