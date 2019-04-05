package org.innovateuk.ifs.application.forms.sections.yourprojectcosts.form;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowType;
import org.innovateuk.ifs.finance.resource.cost.TravelCost;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Digits;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

import static com.google.common.base.Strings.isNullOrEmpty;
import static org.innovateuk.ifs.finance.resource.cost.FinanceRowItem.*;

public class TravelRowForm extends AbstractCostRowForm<TravelCost> {

    @NotBlank(message = NOT_BLANK_MESSAGE)
    @Length(max = MAX_STRING_LENGTH, message = MAX_LENGTH_MESSAGE)
    private String item;

    @NotNull(message = NOT_BLANK_MESSAGE)
    @Min(value = 1, message = VALUE_MUST_BE_HIGHER_MESSAGE)
    @Digits(integer = MAX_DIGITS_INT, fraction = 0, message = NO_DECIMAL_VALUES)
    private Integer times;

    @NotNull(message = NOT_BLANK_MESSAGE)
    @DecimalMin(value = "1", message = VALUE_MUST_BE_HIGHER_MESSAGE)
    @Digits(integer = MAX_DIGITS, fraction = 0, message = NO_DECIMAL_VALUES)
    private BigDecimal eachCost;

    public TravelRowForm() {}

    public TravelRowForm(TravelCost cost) {
        super(cost);
        this.item = cost.getItem();
        this.times = cost.getQuantity();
        this.eachCost = cost.getCost();
    }

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public Integer getTimes() {
        return times;
    }

    public void setTimes(Integer times) {
        this.times = times;
    }

    public BigDecimal getEachCost() {
        return eachCost;
    }

    public void setEachCost(BigDecimal eachCost) {
        this.eachCost = eachCost;
    }

    @Override
    public boolean isBlank() {
        return isNullOrEmpty(item) && times == null && eachCost == null;
    }

    @Override
    public FinanceRowType getRowType() {
        return FinanceRowType.TRAVEL;
    }

    @Override
    public TravelCost toCost() {
        return new TravelCost(getCostId(), item, eachCost, times);
    }
}
