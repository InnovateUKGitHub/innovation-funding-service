package org.innovateuk.ifs.sil.grant.resource;

import java.util.Set;

public class Forecast {
    private String costCategory;
    private Set<Period> periods;

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

    public Set<Period> getPeriods() {
        return periods;
    }

    public void setPeriods(Set<Period> periods) {
        this.periods = periods;
    }

    public Forecast periods(Set<Period> periods) {
        setPeriods(periods);
        return this;
    }
}
