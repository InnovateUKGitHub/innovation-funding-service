package com.worth.ifs.finance.domain;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * CostFields defines database relations and a model to use client side and server side.
 * Holds all the fields which do not belong to the defined general costs.
 */
@Entity
public class CostField {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String title;
    private String type;

    @OneToMany(mappedBy="costField")
    private List<CostValue> costValues = new ArrayList<>();

    public CostField() {
    	// no-arg constructor
    }

    public CostField(Long id, String title, String type) {
        this.id = id;
        this.title = title;
        this.type = type;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getType() {
        return type;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<CostValue> getCostValues() {
        return this.costValues;
    }

    public void setCostValues(List<CostValue> costValues) {
        this.costValues = costValues;
    }
}
