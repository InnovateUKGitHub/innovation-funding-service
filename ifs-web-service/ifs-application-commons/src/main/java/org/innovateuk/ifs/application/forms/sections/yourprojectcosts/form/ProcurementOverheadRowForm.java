package org.innovateuk.ifs.application.forms.sections.yourprojectcosts.form;

import org.hibernate.validator.constraints.Length;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowType;
import org.innovateuk.ifs.finance.resource.cost.ProcurementOverhead;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

import static com.google.common.base.Strings.isNullOrEmpty;
import static org.innovateuk.ifs.finance.resource.cost.FinanceRowItem.*;

public class ProcurementOverheadRowForm extends AbstractCostRowForm<ProcurementOverhead> {

    @NotNull(message = NOT_BLANK_MESSAGE)
    @NotBlank(message = NOT_BLANK_MESSAGE)
    @Length(max = MAX_STRING_LENGTH, message = MAX_LENGTH_MESSAGE)
    private String item;

    @NotNull(message = NOT_BLANK_MESSAGE)
    @DecimalMin(value = "1", message = VALUE_MUST_BE_HIGHER_MESSAGE)
    @Digits(integer = MAX_DIGITS, fraction = 0, message = NO_DECIMAL_VALUES)
    private Integer companyCost;

    @NotNull(message = NOT_BLANK_MESSAGE)
    @DecimalMin(value = "1", message = VALUE_MUST_BE_HIGHER_MESSAGE)
    @Digits(integer = MAX_DIGITS, fraction = 0, message = NO_DECIMAL_VALUES)
    private BigDecimal projectCost;

    public ProcurementOverheadRowForm() {}

    public ProcurementOverheadRowForm(Long targetId, String item, BigDecimal projectCost, Integer companyCost) {
        this.item = item;
        this.projectCost = projectCost;
        this.companyCost = companyCost;
    }

    public ProcurementOverheadRowForm(ProcurementOverhead cost) {
        super(cost);
        this.item = cost.getItem();
        this.projectCost = cost.getProjectCost();
        this.companyCost = cost.getCompanyCost();
    }

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public Integer getCompanyCost() {
        return companyCost;
    }

    public void setCompanyCost(Integer companyCost) {
        this.companyCost = companyCost;
    }

    public BigDecimal getProjectCost() {
        return projectCost;
    }

    public void setProjectCost(BigDecimal projectCost) {
        this.projectCost = projectCost;
    }

    @Override
    public boolean isBlank() {
        return isNullOrEmpty(item) && projectCost == null && companyCost == null;
    }

    @Override
    public FinanceRowType getRowType() {
        return FinanceRowType.PROCUREMENT_OVERHEADS;
    }

    @Override
    public ProcurementOverhead toCost(Long financeId) {
        return new ProcurementOverhead(getCostId(), item, projectCost, companyCost, financeId);
    }
}
