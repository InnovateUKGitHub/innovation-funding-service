package com.worth.ifs.finance.resource.cost;

import java.math.BigDecimal;

public class AcademicCost implements CostItem {
    private Long id;
    private String name;
    private CostType costType;
    private BigDecimal cost;
    private String item;

    public AcademicCost() {
    	// no-arg constructor
        costType = CostType.ACADEMIC;
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
    public CostType getCostType() {
        return CostType.ACADEMIC;
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
