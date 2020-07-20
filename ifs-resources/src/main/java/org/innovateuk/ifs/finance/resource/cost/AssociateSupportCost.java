package org.innovateuk.ifs.finance.resource.cost;

import java.math.BigDecimal;
import java.math.BigInteger;

import static java.util.Optional.ofNullable;


/**
 * {@code AssociateSupportCost} implements {@link FinanceRowItem}
 */
public class AssociateSupportCost extends AbstractFinanceRowItem {

    private Long id;

    private String description;

    private BigInteger cost;

    private AssociateSupportCost() {
        this(null);
    }

    public AssociateSupportCost(Long targetId) {
        super(targetId);
    }

    public AssociateSupportCost(Long targetId, Long id, String description, BigInteger cost) {
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
        return ofNullable(cost).map(BigDecimal::new).orElse(null);
    }

    @Override
    public FinanceRowType getCostType() {
        return FinanceRowType.ASSOCIATE_SUPPORT;
    }

    @Override
    public String getName() {
        return "Associate Support Cost";
    }

    @Override
    public boolean isEmpty() {
        return false;
    }
}
