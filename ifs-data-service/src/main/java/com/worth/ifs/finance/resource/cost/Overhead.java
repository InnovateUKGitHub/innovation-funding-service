package com.worth.ifs.finance.resource.cost;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.math.BigDecimal;

/**
 * {@code Overhead} implements {@link CostItem}
 *
 */
public class Overhead implements CostItem {
    private Long id;
    @Enumerated(EnumType.STRING)
    private OverheadRateType rateType;
    @Min(0)
    @Max(100)
    private Integer rate;
    private CostType costType;
    private String name;

    public Overhead() {
        this.costType = CostType.OVERHEADS;
        this.rateType = OverheadRateType.NONE;
        this.name = this.costType.getType();
    }

    public Overhead(Long id, OverheadRateType rateType, Integer rate) {
        this();
        this.id = id;
        this.rateType = rateType;
        this.rate = rate;
    }

    public Integer getRate(){
        return rate;
    }


    @Override
    public BigDecimal getTotal() {
        return BigDecimal.ZERO;
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public CostType getCostType() {
        return costType;
    }

    public OverheadRateType getRateType() {
        return rateType;
    }

    public void setRateType(OverheadRateType rateType) {
        this.rateType = rateType;
    }

    @Override
    public String getName() {
        return name;
    }
}

