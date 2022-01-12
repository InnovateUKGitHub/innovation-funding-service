package org.innovateuk.ifs.finance.resource.cost;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class GrantClaimAmount extends AbstractFinanceRowItem implements GrantClaim {
    private Long id;
    private BigDecimal amount;

    private GrantClaimAmount() {
        this(null);
    }

    public GrantClaimAmount(Long targetId) {
        super(targetId);
    }

    public GrantClaimAmount(Long id, BigDecimal amount, Long targetId) {
        this(targetId);
        this.id = id;
        this.amount = amount;
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public BigDecimal getTotal() {
        return amount;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    @Override
    @JsonIgnore
    public String getName() {
        return getCostType().getType();
    }

    @Override
    @JsonIgnore
    public boolean isEmpty() {
        return false;
    }

    @Override
    @JsonIgnore
    public FinanceRowType getCostType() {
        return FinanceRowType.GRANT_CLAIM_AMOUNT;
    }

    @Override
    public void reset() {
        // The grant claim amount doesn't get reset by research cat or organisation size.
    }

    @Override
    public BigDecimal calculateClaimPercentage(BigDecimal total, BigDecimal totalOtherFunding) {
        if (amount == null || total.compareTo(totalOtherFunding) == 0 || total.equals(BigDecimal.ZERO)) {
            return BigDecimal.ZERO;
        }
        BigDecimal roundedCosts = total.setScale(0, BigDecimal.ROUND_HALF_EVEN); //Same as thymeleaf
        return amount.add(totalOtherFunding)
                .multiply(new BigDecimal(100))
                .divide(roundedCosts, 2, RoundingMode.HALF_UP);
    }

    @Override
    public BigDecimal calculateFundingSought(BigDecimal total, BigDecimal totalOtherFunding) {
        return (amount == null ? BigDecimal.ZERO : amount)
                .max(BigDecimal.ZERO);
    }

    @Override
    @JsonIgnore
    public boolean isRequestingFunding() {
        return amount != null && !amount.equals(BigDecimal.ZERO);
    }

}
