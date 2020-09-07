package org.innovateuk.ifs.finance.resource.cost;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

public class Justification extends AbstractFinanceRowItem {

    private Long id;

    @NotNull(message = NOT_BLANK_MESSAGE)
    private Boolean exceedAllowedLimit;

    private String explanation;

    public Justification() {
        super(null);
    }

    public Justification(Long targetId) {
        super(targetId);
    }

    public Justification(Long targetId, Long id, Boolean exceedAllowedLimit, String explanation) {
        super(targetId);
        this.id = id;
        this.exceedAllowedLimit = exceedAllowedLimit;
        this.explanation = explanation;
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Boolean getExceedAllowedLimit() {
        return exceedAllowedLimit;
    }

    public void setExceedAllowedLimit(Boolean exceedAllowedLimit) {
        this.exceedAllowedLimit = exceedAllowedLimit;
    }

    public String getExplanation() {
        return explanation;
    }

    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }

    @Override
    public BigDecimal getTotal() {
        return BigDecimal.ZERO;
    }

    @Override
    public FinanceRowType getCostType() {
        return FinanceRowType.JUSTIFICATION;
    }

    @Override
    public String getName(){
        return getCostType().getDisplayName();
    }

    @Override
    public boolean isEmpty() {
        return false;
    }
}
