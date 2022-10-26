package org.innovateuk.ifs.application.forms.sections.yourprojectcosts.form;

import org.innovateuk.ifs.finance.resource.cost.FinanceRowType;
import org.innovateuk.ifs.finance.resource.cost.LabourCost;
import org.innovateuk.ifs.finance.resource.cost.PersonnelCost;

import javax.validation.constraints.*;
import java.math.BigDecimal;

import static com.google.common.base.Strings.isNullOrEmpty;
import static org.innovateuk.ifs.finance.resource.cost.FinanceRowItem.*;

public class PersonnelRowForm extends AbstractCostRowForm<PersonnelCost> {

    @Size(max = MAX_STRING_LENGTH, message = MAX_LENGTH_MESSAGE)
    @NotBlank(message = NOT_BLANK_MESSAGE)
    private String role;

    @NotNull(message = NOT_BLANK_MESSAGE)
    @DecimalMin(value = "1", message = VALUE_MUST_BE_HIGHER_MESSAGE)
    @Digits(integer = MAX_DIGITS, fraction = 0, message = NO_DECIMAL_VALUES)
    private BigDecimal gross;

    @NotNull(message = NOT_BLANK_MESSAGE)
    @Min(value=1, message = VALUE_MUST_BE_HIGHER_MESSAGE)
    @Digits(integer = MAX_DIGITS_INT, fraction = 0, message = NO_DECIMAL_VALUES)
    private Integer days;

    @NotNull(message = NOT_BLANK_MESSAGE)
    @DecimalMin(value = "1", message = VALUE_MUST_BE_HIGHER_MESSAGE)
    @Digits(integer = MAX_DIGITS, fraction = 0, message = NO_DECIMAL_VALUES)
    private BigDecimal rate;

    private boolean thirdPartyOfgem;

    public PersonnelRowForm() {
        super();
    }

    public PersonnelRowForm(boolean thirdPartyOfgem) {
        super();
        this.thirdPartyOfgem = thirdPartyOfgem;
    }

    public PersonnelRowForm(PersonnelCost cost) {
        super(cost);
        this.role = cost.getRole();
        this.days = cost.getLabourDays();
        this.gross = cost.getGrossEmployeeCost();
        this.rate = cost.getRate();
        this.thirdPartyOfgem = cost.isThirdPartyOfgem();
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public BigDecimal getGross() {
        return gross;
    }

    public void setGross(BigDecimal gross) {
        this.gross = gross;
    }

    public Integer getDays() {
        return days;
    }

    public void setDays(Integer days) {
        this.days = days;
    }

    public BigDecimal getRate() {
        return rate;
    }

    public void setRate(BigDecimal rate) {
        this.rate = rate;
    }

    public boolean getThirdPartyOfgem() {
        return thirdPartyOfgem;
    }

    public void setThirdPartyOfgem(boolean thirdPartyOfgem) {
        this.thirdPartyOfgem = thirdPartyOfgem;
    }

    @Override
    public boolean isBlank() {
        return isNullOrEmpty(role)
                && (thirdPartyOfgem ? rate == null : gross == null)
                && days == null;
    }

    @Override
    public FinanceRowType getRowType() {
        return FinanceRowType.PERSONNEL;
    }

    @Override
    public PersonnelCost toCost(Long financeId) {
        return new PersonnelCost(getCostId(), null, role, gross, days, null, financeId, rate, thirdPartyOfgem);
    }
}