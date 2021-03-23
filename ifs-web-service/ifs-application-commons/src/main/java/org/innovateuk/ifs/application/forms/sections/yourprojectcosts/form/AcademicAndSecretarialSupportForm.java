package org.innovateuk.ifs.application.forms.sections.yourprojectcosts.form;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

import static org.innovateuk.ifs.finance.resource.cost.FinanceRowItem.*;
import static org.innovateuk.ifs.finance.resource.cost.FinanceRowItem.NO_DECIMAL_VALUES;

public class AcademicAndSecretarialSupportForm {

    @NotNull(message = NOT_BLANK_MESSAGE)
    @DecimalMin(value = "1", message = VALUE_MUST_BE_HIGHER_MESSAGE)
    @Digits(integer = MAX_DIGITS, fraction = 0, message = NO_DECIMAL_VALUES)
    private BigDecimal cost;

    public boolean isBlank() {
        return false;
    }

    public BigDecimal getCost() {
        return cost;
    }

    public void setCost(BigDecimal cost) {
        this.cost = cost;
    }
}
