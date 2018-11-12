package org.innovateuk.ifs.application.forms.yourprojectcosts.form;

import org.innovateuk.ifs.finance.resource.cost.FinanceRowType;
import org.innovateuk.ifs.finance.resource.cost.LabourCost;

import java.math.BigDecimal;

import static com.google.common.base.Strings.isNullOrEmpty;

public class LabourRowForm extends AbstractCostRowForm<LabourCost> {

    private String role;

    private BigDecimal gross;

    private Integer days;

    private BigDecimal rate = BigDecimal.ZERO;

    public LabourRowForm() {
        super();
    }

    public LabourRowForm(LabourCost cost) {
        super(cost);
        this.role = cost.getRole();
        this.days = cost.getLabourDays();
        this.gross = cost.getGrossEmployeeCost();
        this.rate = cost.getRate();
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

    @Override
    public boolean isBlank() {
        return isNullOrEmpty(role) && gross == null && days == null;
    }

    @Override
    public FinanceRowType getRowType() {
        return FinanceRowType.LABOUR;
    }

    @Override
    public LabourCost toCost() {
        return new LabourCost(getCostId(), null, role, gross, days, null);
    }
}
