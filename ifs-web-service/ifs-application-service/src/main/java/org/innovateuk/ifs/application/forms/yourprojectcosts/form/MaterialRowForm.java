package org.innovateuk.ifs.application.forms.yourprojectcosts.form;

import org.innovateuk.ifs.finance.resource.cost.FinanceRowType;
import org.innovateuk.ifs.finance.resource.cost.Materials;

import java.math.BigDecimal;

import static com.google.common.base.Strings.isNullOrEmpty;

public class MaterialRowForm extends AbstractCostRowForm<Materials> {

    private String item;

    private Integer quantity;

    private BigDecimal cost;

    public MaterialRowForm() { }

    public MaterialRowForm(Materials cost) {
        super(cost);
        this.item = cost.getItem();
        this.quantity = cost.getQuantity();
        this.cost = cost.getCost();
    }

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
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

    @Override
    public boolean isBlank() {
        return isNullOrEmpty(item) && quantity == null && cost == null;
    }

    @Override
    public FinanceRowType getRowType() {
        return FinanceRowType.MATERIALS;
    }

    @Override
    public Materials toCost() {
        return new Materials(getCostId(), item, cost, quantity);
    }
}
