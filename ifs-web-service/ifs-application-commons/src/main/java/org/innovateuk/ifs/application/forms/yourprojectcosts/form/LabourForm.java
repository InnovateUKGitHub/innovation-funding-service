package org.innovateuk.ifs.application.forms.yourprojectcosts.form;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.innovateuk.ifs.finance.resource.cost.FinanceRowItem.*;

public class LabourForm {

    @Min(value=1, message = VALUE_MUST_BE_HIGHER_MESSAGE)
    @Max(value=365, message = VALUE_MUST_BE_LOWER_MESSAGE)
    @NotNull(message = NOT_BLANK_MESSAGE)
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
