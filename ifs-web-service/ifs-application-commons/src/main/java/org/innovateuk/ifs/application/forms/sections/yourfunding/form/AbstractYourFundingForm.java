package org.innovateuk.ifs.application.forms.sections.yourfunding.form;

import org.innovateuk.ifs.finance.resource.cost.FinanceRowType;

import java.math.BigDecimal;
import java.util.*;

import static org.innovateuk.ifs.finance.resource.cost.FinanceRowType.FINANCE;
import static org.innovateuk.ifs.finance.resource.cost.FinanceRowType.GRANT_CLAIM_AMOUNT;

public abstract class AbstractYourFundingForm {

    private Boolean otherFunding;

    private Map<String, BaseOtherFundingRowForm> otherFundingRows = Collections.emptyMap();

    public Boolean getOtherFunding() {
        return otherFunding;
    }

    public void setOtherFunding(Boolean otherFunding) {
        this.otherFunding = otherFunding;
    }

    public Map<String, BaseOtherFundingRowForm> getOtherFundingRows() {
        return otherFundingRows;
    }

    public void setOtherFundingRows(Map<String, BaseOtherFundingRowForm> otherFundingRows) {
        this.otherFundingRows = otherFundingRows;
    }

    public Map<String, BaseOtherFundingRowForm> getPreviousFundingRows() {
        return otherFundingRows;
    }

    public void setPreviousFundingRows(Map<String, BaseOtherFundingRowForm> otherFundingRows) {
        this.otherFundingRows = otherFundingRows;
    }

    public BigDecimal getOtherFundingTotal() {
        return otherFundingRows == null ? BigDecimal.ZERO :
                otherFundingRows.entrySet().stream()
                        .map(Map.Entry::getValue)
                        .map(BaseOtherFundingRowForm::getFundingAmount)
                        .filter(Objects::nonNull)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    protected abstract FinanceRowType financeType();

    public boolean isFundingPercentage() {
        return FINANCE.equals(financeType());
    }
    public boolean isFundingAmount() {
        return GRANT_CLAIM_AMOUNT.equals(financeType());
    }
}