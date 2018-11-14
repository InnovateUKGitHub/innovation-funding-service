package org.innovateuk.ifs.application.forms.yourprojectcosts.form;

import org.innovateuk.ifs.finance.resource.cost.LabourCost;

import javax.validation.constraints.Digits;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.groups.Default;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.innovateuk.ifs.finance.resource.cost.FinanceRowItem.*;

public class LabourForm {

    @Min(value=1, groups = LabourCost.YearlyWorkingDays.class, message = VALUE_MUST_BE_HIGHER_MESSAGE)
    @NotNull(groups = Default.class, message = NOT_BLANK_MESSAGE)
    @Digits(integer = MAX_DIGITS_INT, fraction = 0, message = NO_DECIMAL_VALUES)
    private Integer workingDaysPerYear;

    private Map<String, LabourRowForm> rows = new LinkedHashMap<>();

    public Integer getWorkingDaysPerYear() {
        return workingDaysPerYear;
    }

    public void setWorkingDaysPerYear(Integer workingDaysPerYear) {
        this.workingDaysPerYear = workingDaysPerYear;
    }

    public Map<String, LabourRowForm> getRows() {
        return rows;
    }

    public void setRows(Map<String, LabourRowForm> rows) {
        this.rows = rows;
    }
}
