package org.innovateuk.ifs.application.forms.yourfunding.form;

import java.math.BigDecimal;

import static com.google.common.base.Strings.isNullOrEmpty;

public class OtherFundingRowForm {

    private Long costId;
    private String source;
    private String date;
    private BigDecimal fundingAmount;

    public Long getCostId() {
        return costId;
    }

    public void setCostId(Long costId) {
        this.costId = costId;
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

    public boolean isBlank() {
        return isNullOrEmpty(source) && isNullOrEmpty(date) && fundingAmount == null;
    }
}
