package org.innovateuk.ifs.application.forms.yourprojectcosts.form;


import org.innovateuk.ifs.finance.resource.cost.FinanceRowItem;

import java.math.BigDecimal;

public abstract class AbstractCostRowForm<F extends FinanceRowItem> {

    private Long costId;

    private BigDecimal total = BigDecimal.ZERO;

    public AbstractCostRowForm() {
    }

    public AbstractCostRowForm(F rowItem) {
        this.costId = rowItem.getId();
        this.total = rowItem.getTotal();
    }

    public Long getCostId() {
        return costId;
    }

    public void setCostId(Long costId) {
        this.costId = costId;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public BigDecimal getTotal() {
        return total;
    }

}
