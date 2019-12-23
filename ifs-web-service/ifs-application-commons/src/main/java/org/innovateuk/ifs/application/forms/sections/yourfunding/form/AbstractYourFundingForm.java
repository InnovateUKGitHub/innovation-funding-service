package org.innovateuk.ifs.application.forms.sections.yourfunding.form;

import org.innovateuk.ifs.finance.resource.cost.FinanceRowType;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

import static org.innovateuk.ifs.finance.resource.cost.FinanceRowType.FINANCE;
import static org.innovateuk.ifs.finance.resource.cost.FinanceRowType.GRANT_CLAIM_AMOUNT;

public abstract class AbstractYourFundingForm {

    private Boolean otherFunding;

    private Map<String, OtherFundingRowForm> otherFundingRows = new LinkedHashMap<>();

    public Boolean getOtherFunding() {
        return otherFunding;
    }

    public void setOtherFunding(Boolean otherFunding) {
        this.otherFunding = otherFunding;
    }

    public Map<String, OtherFundingRowForm> getOtherFundingRows() {
        return otherFundingRows;
    }

    public void setOtherFundingRows(Map<String, OtherFundingRowForm> otherFundingRows) {
        this.otherFundingRows = otherFundingRows;
    }

    public BigDecimal getOtherFundingTotal() {
        return otherFundingRows == null ? BigDecimal.ZERO :
                otherFundingRows.entrySet().stream()
                        .map(Map.Entry::getValue)
                        .map(OtherFundingRowForm::getFundingAmount)
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