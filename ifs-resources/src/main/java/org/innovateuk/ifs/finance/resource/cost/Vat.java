package org.innovateuk.ifs.finance.resource.cost;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

public class Vat extends AbstractFinanceRowItem {
    private Long id;

    @NotNull(message = NOT_BLANK_MESSAGE)
    private Boolean registered;

    private BigDecimal rate;

    public Vat() {
        this(null);
    }

    public Vat(Long targetId) {
        super(targetId);
    }

    public Vat(Long id, Boolean registered, BigDecimal rate, Long targetId) {
        this(targetId);
        this.id = id;
        this.registered = registered;
        this.rate = rate;
    }

    @Override
    public Long getId() {
        return id;
    }

    public Boolean getRegistered() {
        return registered;
    }

    public void setRegistered(Boolean registered) {
        this.registered = registered;
    }

    @Override
    public BigDecimal getTotal() {
        return BigDecimal.ZERO;
    }

    @Override
    public BigDecimal totalDiff(FinanceRowItem other) {
        return super.totalDiff(other);
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public FinanceRowType getCostType() {
        return FinanceRowType.VAT;
    }

    @Override
    public String getName(){
        return getCostType().getName();
    }

    public BigDecimal getRate() {
        return rate;
    }
}
