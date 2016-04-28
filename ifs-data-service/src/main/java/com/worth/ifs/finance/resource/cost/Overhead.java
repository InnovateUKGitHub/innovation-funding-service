package com.worth.ifs.finance.resource.cost;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.Digits;
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
    @Digits(integer = MAX_DIGITS, fraction = 0)
    private Integer rate;
    private String name;

    public Overhead() {
        this.rateType = OverheadRateType.NONE;
        this.name = getCostType().getType();
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
        return  CostType.OVERHEADS;
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

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public int getMinRows() {
        return 0;
    }
}

