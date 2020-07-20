package org.innovateuk.ifs.finance.resource.cost;

import java.math.BigDecimal;
import java.math.BigInteger;


/**
 * {@code EstateCost} implements {@link FinanceRowItem}
 */
public class EstateCost extends AbstractFinanceRowItem {

    private Long id;

    private String description;

    private BigInteger cost;

    private EstateCost() {
        this(null);
    }

    public EstateCost(Long targetId) {
        super(targetId);
    }

    public EstateCost(Long targetId, Long id, String description, BigInteger cost) {
        super(targetId);
        this.id = id;
        this.description = description;
        this.cost = cost;
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigInteger getCost() {
        return cost;
    }

    public void setCost(BigInteger cost) {
        this.cost = cost;
    }

    @Override
    public BigDecimal getTotal() {
        return new BigDecimal(cost);
    }

    @Override
    public FinanceRowType getCostType() {
        return FinanceRowType.ESTATE_COSTS;
    }

    @Override
    public String getName() {
        return "Estate Cost";
    }

    @Override
    public boolean isEmpty() {
        return false;
    }
}
