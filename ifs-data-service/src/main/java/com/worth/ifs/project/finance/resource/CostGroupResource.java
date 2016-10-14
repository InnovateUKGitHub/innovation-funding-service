package com.worth.ifs.project.finance.resource;

import java.util.ArrayList;
import java.util.List;

public class CostGroupResource {

    private Long id;

    private List<CostResource> costs = new ArrayList<>();

    private String description;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<CostResource> getCosts() {
        return costs;
    }

    public void setCosts(List<CostResource> costs) {
        this.costs = costs;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
