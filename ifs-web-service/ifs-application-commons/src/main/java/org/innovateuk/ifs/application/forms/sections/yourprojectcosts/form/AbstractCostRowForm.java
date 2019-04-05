package org.innovateuk.ifs.application.forms.sections.yourprojectcosts.form;


import org.innovateuk.ifs.finance.resource.cost.FinanceRowItem;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowType;

import java.math.BigDecimal;
import java.util.UUID;

public abstract class AbstractCostRowForm<F extends FinanceRowItem> {
    public static final String UNSAVED_ROW_PREFIX = "unsaved-";

    public static String generateUnsavedRowId() {
        return UNSAVED_ROW_PREFIX + UUID.randomUUID().toString();
    }

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

    public abstract boolean isBlank();

    public abstract FinanceRowType getRowType();

    public abstract F toCost();
}
