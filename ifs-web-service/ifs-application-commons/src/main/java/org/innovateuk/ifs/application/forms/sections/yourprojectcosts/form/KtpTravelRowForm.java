package org.innovateuk.ifs.application.forms.sections.yourprojectcosts.form;

import org.hibernate.validator.constraints.Length;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowType;
import org.innovateuk.ifs.finance.resource.cost.KtpTravelCost;
import org.innovateuk.ifs.finance.resource.cost.KtpTravelCost.KtpTravelCostType;

import javax.validation.constraints.*;
import java.math.BigDecimal;

import static com.google.common.base.Strings.isNullOrEmpty;
import static org.innovateuk.ifs.finance.resource.cost.FinanceRowItem.*;

public class KtpTravelRowForm extends AbstractCostRowForm<KtpTravelCost> {

    @NotNull(message = NOT_BLANK_MESSAGE)
    private KtpTravelCostType type;

    @NotBlank(message = NOT_BLANK_MESSAGE)
    @Length(max = MAX_STRING_LENGTH, message = MAX_LENGTH_MESSAGE)
    private String description;

    @NotNull(message = NOT_BLANK_MESSAGE)
    @Min(value = 1, message = VALUE_MUST_BE_HIGHER_MESSAGE)
    @Digits(integer = MAX_DIGITS_INT, fraction = 0, message = NO_DECIMAL_VALUES)
    private Integer times;

    @NotNull(message = NOT_BLANK_MESSAGE)
    @DecimalMin(value = "1", message = VALUE_MUST_BE_HIGHER_MESSAGE)
    @Digits(integer = MAX_DIGITS, fraction = 0, message = NO_DECIMAL_VALUES)
    private BigDecimal eachCost;

    public KtpTravelRowForm() {}

    public KtpTravelRowForm(KtpTravelCost cost) {
        super(cost);
        this.type = cost.getType();
        this.description = cost.getDescription();
        this.times = cost.getQuantity();
        this.eachCost = cost.getCost();
    }

    public KtpTravelCostType getType() {
        return type;
    }

    public void setType(KtpTravelCostType type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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
        return type == null && isNullOrEmpty(description) && times == null && eachCost == null;
    }

    @Override
    public FinanceRowType getRowType() {
        return FinanceRowType.KTP_TRAVEL;
    }

    @Override
    public KtpTravelCost toCost(Long financeId) {
        return new KtpTravelCost(getCostId(), type, description, eachCost, times, financeId);
    }
}
