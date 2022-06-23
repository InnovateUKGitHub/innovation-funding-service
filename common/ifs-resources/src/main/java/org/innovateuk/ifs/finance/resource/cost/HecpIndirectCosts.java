package org.innovateuk.ifs.finance.resource.cost;

import javax.validation.constraints.Digits;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.groups.Default;
import java.math.BigDecimal;

/**
 * {@code HecpIndirectCosts} implements {@link FinanceRowItem}
 *
 */
public class HecpIndirectCosts extends AbstractFinanceRowItem {

    public final static String FINANCE_HECP_INDIRECT_COSTS_FILE_REQUIRED = "{validation.finance.hecpIndirectCosts.file.required}";

    public interface RateNotZero{}
    public interface TotalCost{}
    private Long id;
    private OverheadRateType rateType;

    @Min.List({
        @Min(value = 0, groups = Default.class, message = VALUE_MUST_BE_HIGHER_MESSAGE),
        @Min(value = 1, groups = RateNotZero.class, message = VALUE_MUST_BE_HIGHER_MESSAGE)
    })
    @Max(value = 100, groups = RateNotZero.class, message = VALUE_MUST_BE_LOWER_MESSAGE)
    @Digits(integer = MAX_DIGITS_INT, fraction = 0, message = MAX_DIGITS_MESSAGE)
    private Integer rate;

    private HecpIndirectCosts() {
        this(null);
    }

    public HecpIndirectCosts(Long targetId) {
        super(targetId);
        this.rateType = OverheadRateType.NONE;
    }

    public HecpIndirectCosts(Long id, OverheadRateType rateType, Integer rate, Long targetId) {
        this(targetId);
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
        return  FinanceRowType.HECP_INDIRECT_COSTS;
    }

    public OverheadRateType getRateType() {
        return rateType;
    }

    public void setRateType(OverheadRateType rateType) {
        this.rateType = rateType;
    }

    @Override
    public String getName() {
        return getCostType().getType();
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    public void setRate(Integer rate) {
        this.rate = rate;
    }
}

