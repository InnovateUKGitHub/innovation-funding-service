package com.worth.ifs.finance.resource.cost;

import javax.validation.constraints.Digits;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.groups.Default;
import java.math.BigDecimal;

/**
 * {@code Overhead} implements {@link FinanceRowItem}
 *
 */
public class Overhead implements FinanceRowItem {
    public interface RateNotZero{}
    private Long id;
    private OverheadRateType rateType;

    @Min.List({
        @Min(value = 0, groups = Default.class, message = VALUE_MUST_BE_HIGHER_MESSAGE),
        @Min(value = 1, groups = RateNotZero.class, message = VALUE_MUST_BE_HIGHER_MESSAGE)
    })
    @Max(value = 100, groups = RateNotZero.class, message = VALUE_MUST_BE_LOWER_MESSAGE)
    @Digits(integer = MAX_DIGITS_INT, fraction = 0, message = MAX_DIGITS_MESSAGE)
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
    public FinanceRowType getCostType() {
        return  FinanceRowType.OVERHEADS;
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

    public void setRate(Integer rate) {
        this.rate = rate;
    }
}

