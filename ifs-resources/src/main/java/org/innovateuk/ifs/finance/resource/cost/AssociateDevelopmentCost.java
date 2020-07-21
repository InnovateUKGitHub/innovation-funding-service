package org.innovateuk.ifs.finance.resource.cost;

import java.math.BigDecimal;
import java.math.BigInteger;

import static java.util.Optional.ofNullable;


/**
 * {@code AssociateDevelopmentCost} implements {@link FinanceRowItem}
 */
public class AssociateDevelopmentCost extends AbstractFinanceRowItem {

    private Long id;

    private String role;

    private Integer duration;

    private BigInteger cost;

    private AssociateDevelopmentCost() {
        this(null);
    }

    public AssociateDevelopmentCost(Long targetId) {
        super(targetId);
    }

    public AssociateDevelopmentCost(Long targetId, Long id, String role, Integer duration, BigInteger cost) {
        super(targetId);
        this.id = id;
        this.role = role;
        this.duration = duration;
        this.cost = cost;
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
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
        return FinanceRowType.ASSOCIATE_DEVELOPMENT_COSTS;
    }

    @Override
    public String getName() {
        return "Associate Development Cost";
    }

    @Override
    public boolean isEmpty() {
        return false;
    }
}
