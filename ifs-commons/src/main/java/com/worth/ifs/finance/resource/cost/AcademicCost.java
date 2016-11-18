package com.worth.ifs.finance.resource.cost;

import javax.validation.constraints.DecimalMin;
import java.math.BigDecimal;

public class AcademicCost implements FinanceRowItem {
    private Long id;
    private String name;

    @DecimalMin(value = "0", message = VALUE_MUST_BE_HIGHER_MESSAGE)
    private BigDecimal cost;

    private String item;

    public AcademicCost() {
    	// no-arg constructor
    }

    public AcademicCost(Long id, String name, BigDecimal cost, String item) {
        this();
        this.id = id;
        this.name = name;
        if(cost != null){
            this.cost = cost.setScale(2, BigDecimal.ROUND_HALF_UP);
        }
        this.item = item;
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
        return FinanceRowType.ACADEMIC;
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
}
