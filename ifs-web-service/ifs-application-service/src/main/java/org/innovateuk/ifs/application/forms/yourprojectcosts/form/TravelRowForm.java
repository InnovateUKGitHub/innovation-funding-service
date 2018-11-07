package org.innovateuk.ifs.application.forms.yourprojectcosts.form;

import org.innovateuk.ifs.finance.resource.cost.TravelCost;

import java.math.BigDecimal;

public class TravelRowForm extends AbstractCostRowForm<TravelCost> {

    private String item;

    private Integer times;

    private BigDecimal eachCost;

    public TravelRowForm() {}

    public TravelRowForm(TravelCost cost) {
        super(cost);
        this.item = cost.getItem();
        this.times = cost.getQuantity();
        this.eachCost = cost.getCost();
    }

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public Integer getTimes() {
        return times;
    }

    public void setTimes(Integer times) {
        this.times = times;
    }

    public BigDecimal getEachCost() {
        return eachCost;
    }

    public void setEachCost(BigDecimal eachCost) {
        this.eachCost = eachCost;
    }
}
