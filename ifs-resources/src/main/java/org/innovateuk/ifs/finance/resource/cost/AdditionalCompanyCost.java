package org.innovateuk.ifs.finance.resource.cost;

import java.math.BigDecimal;
import java.math.BigInteger;

import static java.util.Optional.ofNullable;


/**
 * {@code AdditionalCompanyCost} implements {@link FinanceRowItem}
 */
public class AdditionalCompanyCost extends AbstractFinanceRowItem {

    public enum AdditionalCompanyCostType {
        ASSOCIATE_SALARY,
        MANAGEMENT_SUPERVISION,
        OTHER_STAFF,
        CAPITAL_EQUIPMENT,
        OTHER_COSTS
    }

    private Long id;

    private AdditionalCompanyCostType type;

    private String description;

    private BigInteger cost;

    private AdditionalCompanyCost() {
        this(null);
    }

    public AdditionalCompanyCost(Long targetId) {
        super(targetId);
    }

    public AdditionalCompanyCost(Long targetId, AdditionalCompanyCostType type) {
        super(targetId);
        this.type = type;
    }

    public AdditionalCompanyCost(Long targetId, Long id, AdditionalCompanyCostType type, String description, BigInteger cost) {
        super(targetId);
        this.id = id;
        this.type = type;
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

    public AdditionalCompanyCostType getType() {
        return type;
    }

    public void setType(AdditionalCompanyCostType type) {
        this.type = type;
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
        return FinanceRowType.ADDITIONAL_COMPANY_COSTS;
    }

    @Override
    public String getName() {
        return type.name();
    }

    @Override
    public boolean isEmpty() {
        return false;
    }
}
