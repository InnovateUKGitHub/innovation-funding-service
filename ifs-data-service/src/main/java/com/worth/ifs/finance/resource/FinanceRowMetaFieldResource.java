package com.worth.ifs.finance.resource;

import java.util.ArrayList;
import java.util.List;

public class FinanceRowMetaFieldResource {
    Long id;
    String title;
    String type;
    private List<FinanceRowMetaValueId> costValues = new ArrayList<>();

    public FinanceRowMetaFieldResource() {
    	// no-arg constructor
    }

    public FinanceRowMetaFieldResource(Long id, String title, String type) {
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

    public List<FinanceRowMetaValueId> getCostValues() {
        return this.costValues;
    }

    public void setCostValues(List<FinanceRowMetaValueId> costValues) {
        this.costValues = costValues;
    }
}
