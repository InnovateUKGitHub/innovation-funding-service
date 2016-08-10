package com.worth.ifs.finance.resource;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class FinanceRowResource {
    private Long id;
    private String name;
    private String item;
    private String description;
    private Integer quantity;
    private BigDecimal cost;
    private List<FinanceRowMetaValueResource> costValues = new ArrayList<>();
    private Long applicationFinance;
    private Long question;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getCost() {
        return cost;
    }

    public void setCost(BigDecimal cost) {
        this.cost = cost;
    }

    public List<FinanceRowMetaValueResource> getCostValues() {
        return costValues;
    }

    public void setCostValues(List<FinanceRowMetaValueResource> costValues) {
        this.costValues = costValues;
    }

    public Long getApplicationFinance() {
        return applicationFinance;
    }

    public void setApplicationFinance(Long applicationFinance) {
        this.applicationFinance = applicationFinance;
    }

    public Long getQuestion() {
        return question;
    }

    public void setQuestion(Long question) {
        this.question = question;
    }
}
