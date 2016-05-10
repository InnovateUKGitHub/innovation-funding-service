package com.worth.ifs.finance.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.worth.ifs.application.domain.Question;
import org.hibernate.validator.constraints.Length;
import org.springframework.util.StringUtils;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Cost defines database relations and a model to use client side and server side.
 */
@Entity
public class Cost {
    public static final int DB_MAX_STRING_LENGTH = 255;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id;

    @Length(max = DB_MAX_STRING_LENGTH)
    String item;
    @Length(max = DB_MAX_STRING_LENGTH)
    String description;

    Integer quantity;
    BigDecimal cost;

    @Length(max = DB_MAX_STRING_LENGTH)
    String name;

    @OneToMany(mappedBy="cost")
    private List<CostValue> costValues = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name="applicationFinanceId", referencedColumnName="id")
    private ApplicationFinance applicationFinance;


    @ManyToOne
    @JoinColumn(name="questionId", referencedColumnName="id")
    private Question question;

    public Cost() {
    	// no-arg constructor
    }

    public Cost(String name, String item, String description, Integer quantity, BigDecimal cost,
                ApplicationFinance applicationFinance, Question question) {
        this.name = name;
        this.item = item;
        this.description = description;
        this.quantity = quantity;
        this.cost = cost;
        this.applicationFinance = applicationFinance;
        this.question = question;
    }

    public Cost(Long id, String name, String item, String description, Integer quantity, BigDecimal cost,
                ApplicationFinance applicationFinance, Question question) {
        this(name, item ,description, quantity, cost, applicationFinance, question);
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        // Fix for sql breaking when saving string longer than the field length
        return (StringUtils.hasText(name) && name.length() > DB_MAX_STRING_LENGTH) ? name.substring(0, DB_MAX_STRING_LENGTH) : name;
    }

    public String getItem() {
        // Fix for sql breaking when saving string longer than the field length
        return (StringUtils.hasText(item) && item.length() > DB_MAX_STRING_LENGTH) ? item.substring(0, DB_MAX_STRING_LENGTH) : item;
    }

    public String getDescription() {
        // Fix for sql breaking when saving string longer than the field length
        return (StringUtils.hasText(description) && description.length() > DB_MAX_STRING_LENGTH) ? description.substring(0, DB_MAX_STRING_LENGTH) : description;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public BigDecimal getCost() {
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

    public void setName(String name) {
        this.name = name;
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

    public void setCost(BigDecimal cost) {
        this.cost = cost;
    }

    public void setQuestion(Question question) {
        this.question = question;
    }

    @JsonIgnore
    public ApplicationFinance getApplicationFinance() {
        return this.applicationFinance;
    }
}
