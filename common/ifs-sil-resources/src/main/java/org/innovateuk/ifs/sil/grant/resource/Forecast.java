package org.innovateuk.ifs.sil.grant.resource;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Collection;

public class Forecast {
    @JsonProperty("costcatName")
    private String costCategory;
    @JsonProperty("profile")
    private Collection<Period> periods;

    @JsonProperty("golCost")
    private long cost;

    public String getCostCategory() {
        return costCategory;
    }

    public void setCostCategory(String costCategory) {
        this.costCategory = costCategory;
    }

    public Forecast costCategory(String costCategory) {
        setCostCategory(costCategory);
        return this;
    }

    public Collection<Period> getPeriods() {
        return periods;
    }

    public void setPeriods(Collection<Period> periods) {
        this.periods = periods;
    }

    public Forecast periods(Collection<Period> periods) {
        setPeriods(periods);
        return this;
    }

    public long getCost() {
        return cost;
    }

    public void setCost(long cost) {
        this.cost = cost;
    }

    public Forecast cost(long cost) {
        setCost(cost);
        return this;
    }
}
