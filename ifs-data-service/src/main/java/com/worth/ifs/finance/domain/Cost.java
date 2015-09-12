package com.worth.ifs.finance.domain;

import com.worth.ifs.application.domain.Question;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Cost {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id;

    String item;
    String description;
    Integer quantity;
    Double cost;

    @OneToMany(mappedBy="cost")
    private List<CostValue> costValues = new ArrayList<CostValue>();

    @ManyToOne
    @JoinColumn(name="applicationFinanceId", referencedColumnName="id")
    private ApplicationFinance applicationFinance;

    @ManyToOne
    @JoinColumn(name="questionId", referencedColumnName="id")
    private Question question;

    public Cost() {
    }

    public Cost(String item, String description, Integer quantity, Double cost,
                ApplicationFinance applicationFinance, Question question) {
        this.item = item;
        this.description = description;
        this.quantity = quantity;
        this.cost = cost;
        this.applicationFinance = applicationFinance;
        this.question = question;
    }

    public Cost(Long id, String item, String description, Integer quantity, Double cost,
                ApplicationFinance applicationFinance, Question question) {
        this(item, description, quantity, cost, applicationFinance, question);
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public String getItem() {
        return item;
    }

    public String getDescription() {
        return description;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public Double getCost() {
        return cost;
    }

    public void setApplicationFinance(ApplicationFinance applicationFinance) {
        this.applicationFinance = applicationFinance;
    }

    public List<CostValue> getCostValues() {
        return costValues;
    }

    public Question getQuestion() {
        return question;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public void setCost(Double cost) {
        this.cost = cost;
    }

    public void setQuestion(Question question) {
        this.question = question;
    }

}
