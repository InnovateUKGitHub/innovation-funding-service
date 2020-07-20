package org.innovateuk.ifs.finance.resource.cost;

import java.math.BigDecimal;
import java.math.BigInteger;

import static java.util.Optional.ofNullable;


/**
 * {@code AssociateSalaryCost} implements {@link FinanceRowItem}
 */
public class AssociateSalaryCost extends AbstractFinanceRowItem {

    private Long id;

    private String role;

    private BigInteger cost;

    private AssociateSalaryCost() {
        this(null);
    }

    public AssociateSalaryCost(Long targetId) {
        super(targetId);
    }

    public AssociateSalaryCost(Long targetId, Long id, String role, BigInteger cost) {
        super(targetId);
        this.id = id;
        this.role = role;
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
        return FinanceRowType.ASSOCIATE_SALARY_COSTS;
    }

    @Override
    public String getName() {
        return "Associate Salary Cost";
    }

    @Override
    public boolean isEmpty() {
        return false;
    }
}
