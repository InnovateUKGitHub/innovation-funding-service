package com.worth.ifs.finance.resource;

import java.util.ArrayList;
import java.util.List;

public class CostFieldResource {
    Long id;
    String title;
    String type;
    private List<CostValueId> costValues = new ArrayList<>();

    public CostFieldResource() {
    	// no-arg constructor
    }

    public CostFieldResource(Long id, String title, String type) {
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

    public List<CostValueId> getCostValues() {
        return this.costValues;
    }

    public void setCostValues(List<CostValueId> costValues) {
        this.costValues = costValues;
    }
}
