package org.innovateuk.ifs.application.forms.sections.yourfunding.form;

import org.innovateuk.ifs.application.forms.sections.yourprojectcosts.form.AbstractCostRowForm;
import org.innovateuk.ifs.finance.resource.cost.BaseOtherFunding;

import java.math.BigDecimal;

import static com.google.common.base.Strings.isNullOrEmpty;

public abstract class BaseOtherFundingRowForm<T extends BaseOtherFunding> extends AbstractCostRowForm<T> {

    private String source;

    private String date;

    private BigDecimal fundingAmount;

    public BaseOtherFundingRowForm() {
        this(null);
    }

    public BaseOtherFundingRowForm(T fundingRow) {
        super(fundingRow);
        if (fundingRow != null) {
            this.source = fundingRow.getFundingSource();
            this.date = fundingRow.getSecuredDate();
            this.fundingAmount = fundingRow.getFundingAmount();
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
    public boolean isBlank() {
        return isNullOrEmpty(source) && isNullOrEmpty(date) && fundingAmount == null;
    }
}
