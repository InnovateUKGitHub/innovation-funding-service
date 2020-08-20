package org.innovateuk.ifs.application.forms.sections.yourfunding.form;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.innovateuk.ifs.application.forms.sections.yourprojectcosts.form.AbstractCostRowForm;
import org.innovateuk.ifs.finance.resource.EmployeesAndTurnoverResource;
import org.innovateuk.ifs.finance.resource.GrowthTableResource;
import org.innovateuk.ifs.finance.resource.KtpYearsResource;
import org.innovateuk.ifs.finance.resource.cost.BaseOtherFunding;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowType;
import org.innovateuk.ifs.finance.resource.cost.OtherFunding;
import org.innovateuk.ifs.finance.resource.cost.PreviousFunding;

import java.math.BigDecimal;

import static com.google.common.base.Strings.isNullOrEmpty;

public class BaseOtherFundingRowForm<T extends BaseOtherFunding> extends AbstractCostRowForm<T> {

    private String source;

    private String date;

    private BigDecimal fundingAmount;

    private FinanceRowType financeRowType;

    private BaseOtherFundingRowForm() {
        super(null);
    }

    public BaseOtherFundingRowForm(FinanceRowType financeRowType) {
        super(null);
        this.financeRowType = financeRowType;
    }

    public BaseOtherFundingRowForm(T fundingRow) {
        super(fundingRow);
        if (fundingRow != null) {
            this.source = fundingRow.getFundingSource();
            this.date = fundingRow.getSecuredDate();
            this.fundingAmount = fundingRow.getFundingAmount();
            this.financeRowType = fundingRow.getCostType();
        }
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public BigDecimal getFundingAmount() {
        return fundingAmount;
    }

    public void setFundingAmount(BigDecimal fundingAmount) {
        this.fundingAmount = fundingAmount;
    }

    @Override
    public FinanceRowType getRowType() {
        return financeRowType;
    }

    @Override
    public T toCost(Long financeId) {
        if (financeRowType == FinanceRowType.PREVIOUS_FUNDING) {
            return (T) new PreviousFunding(getCostId(), null, getSource(), getDate(), getFundingAmount(), financeId);
        }
        if (financeRowType == FinanceRowType.OTHER_FUNDING) {
            return (T) new OtherFunding(getCostId(), null, getSource(), getDate(), getFundingAmount(), financeId);
        }
        throw new IllegalArgumentException();
    }

    public void setFinanceRowType(FinanceRowType financeRowType) {
        if (FinanceRowType.PREVIOUS_FUNDING != financeRowType && FinanceRowType.OTHER_FUNDING != financeRowType) {
            throw new IllegalArgumentException();
        }
        this.financeRowType = financeRowType;
    }

    @Override
    public boolean isBlank() {
        return isNullOrEmpty(source) && isNullOrEmpty(date) && fundingAmount == null;
    }
}
