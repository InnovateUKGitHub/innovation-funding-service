package org.innovateuk.ifs.finance.resource.cost;

import javax.validation.constraints.Digits;
import javax.validation.groups.Default;
import java.math.BigDecimal;

public class AcademicCost extends AbstractFinanceRowItem {
    private Long id;
    private String name;

    @Digits(integer = MAX_DIGITS, fraction = 0, groups = Default.class, message = NO_DECIMAL_VALUES)
    private BigDecimal cost;

    private String item;

    private FinanceRowType costType;

    public AcademicCost() {
    	// no-arg constructor
    }

    public AcademicCost(Long id, String name, BigDecimal cost, String item, FinanceRowType costType) {
        this();
        this.id = id;
        this.name = name;
        this.cost = cost;
        this.item = item;
        this.costType = costType;
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public BigDecimal getTotal() {
        return cost;
    }

    @Override
    public FinanceRowType getCostType() {
        return costType;
    }

    public String getItem() {
        return item;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public int getMinRows() {
        return 0;
    }

    public BigDecimal getCost() {
        return cost;
    }

    public void setCost(BigDecimal cost) {
        this.cost = cost;
    }

    public void setItem(String item) {
        this.item = item;
    }
}
