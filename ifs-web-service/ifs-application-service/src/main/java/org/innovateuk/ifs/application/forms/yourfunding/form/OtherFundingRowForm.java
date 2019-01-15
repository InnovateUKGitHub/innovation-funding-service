package org.innovateuk.ifs.application.forms.yourfunding.form;

import org.innovateuk.ifs.application.forms.yourprojectcosts.form.AbstractCostRowForm;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowType;
import org.innovateuk.ifs.finance.resource.cost.OtherFunding;

import java.math.BigDecimal;

import static com.google.common.base.Strings.isNullOrEmpty;

public class OtherFundingRowForm extends AbstractCostRowForm<OtherFunding> {

    private String source;

    private String date;

    private BigDecimal fundingAmount;

    public OtherFundingRowForm() {}

    public OtherFundingRowForm(OtherFunding otherFunding) {
        super(otherFunding);
        this.source = otherFunding.getFundingSource();
        this.date = otherFunding.getSecuredDate();
        this.fundingAmount = otherFunding.getFundingAmount();
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

    @Override
    public FinanceRowType getRowType() {
        return FinanceRowType.OTHER_FUNDING;
    }

    @Override
    public OtherFunding toCost() {
        return new OtherFunding(getCostId(), null, getSource(), getDate(), getFundingAmount());
    }
}
