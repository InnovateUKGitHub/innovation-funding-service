package org.innovateuk.ifs.application.forms.sections.yourprojectcosts.form;

import javax.validation.constraints.Length;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowType;
import org.innovateuk.ifs.finance.resource.cost.ProcurementOverhead;

import javax.validation.constraints.*;
import java.math.BigDecimal;

import static com.google.common.base.Strings.isNullOrEmpty;
import static org.innovateuk.ifs.finance.resource.cost.FinanceRowItem.*;

public class ProcurementOverheadRowForm extends AbstractCostRowForm<ProcurementOverhead> {

    private static final BigDecimal ONE_HUNDRED = new BigDecimal(100);

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
    @DecimalMax(value = "100", message = VALUE_MUST_BE_LOWER_MESSAGE)
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

    @Override
    public BigDecimal getTotal() {
        BigDecimal total = BigDecimal.ZERO;

        if (companyCost != null && projectCost != null) {
            total = projectCost.multiply(new BigDecimal(companyCost).divide(ONE_HUNDRED));
            return total;
        }

        return total;
    }
}
