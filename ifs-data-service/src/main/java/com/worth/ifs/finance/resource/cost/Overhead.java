package com.worth.ifs.finance.resource.cost;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.math.BigDecimal;

/**
 * {@code Overhead} implements {@link CostItem}
 */
public class Overhead implements CostItem {
    private Long id;
    @Enumerated(EnumType.STRING)
    private OverheadRateType rateType = OverheadRateType.NONE;
    private Integer customRate;
    private BigDecimal agreedRate;
    private CostType costType;

    public Overhead() {
        this.costType = CostType.OVERHEADS;
    }

    public Overhead(Long id, OverheadRateType rateType, Integer customRate, BigDecimal agreedRate) {
        this();
        this.id = id;
        this.rateType = rateType;
        this.customRate = customRate;
        this.agreedRate = agreedRate;
    }

    public Integer getCustomRate() {
        return customRate;
    }

    public Integer getRate(){
        if(rateType != null && rateType.getRate() != null){
            return rateType.getRate();
        }else if(customRate!= null && !customRate.equals(0)){
            return customRate;
        }else{
            return 99;
        }
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

    public BigDecimal getAgreedRate() {
        return agreedRate;
    }

    public void setAgreedRate(BigDecimal agreedRate) {
        this.agreedRate = agreedRate;
    }
}

