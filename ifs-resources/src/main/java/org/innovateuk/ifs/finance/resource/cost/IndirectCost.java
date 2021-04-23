package org.innovateuk.ifs.finance.resource.cost;

import java.math.BigDecimal;
import java.math.BigInteger;


/**
 * {@code IndirectCosts} implements {@link FinanceRowItem}
 */
public class IndirectCost extends AbstractFinanceRowItem {

    private Long id;

    private BigInteger cost;

    private IndirectCost() {
        this(null);
    }

    public IndirectCost(Long targetId) {
        super(targetId);
    }

    public IndirectCost(Long targetId, Long id, BigInteger cost) {
        super(targetId);
        this.id = id;
        this.cost = cost;
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigInteger getCost() {
        return cost;
    }

    public void setCost(BigInteger cost) {
        this.cost = cost;
    }

    @Override
    public BigDecimal getTotal() {
        return bigDecimalOrNull(cost);
    }

    @Override
    public FinanceRowType getCostType() {
        return FinanceRowType.INDIRECT_COSTS;
    }

    @Override
    public String getName() {
        return this.getCostType().getType();
    }

    @Override
    public boolean isEmpty() {
        return false;
    }
}
