package org.innovateuk.ifs.application.forms.yourfunding.form;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Objects;

public class YourFundingForm {
    public static final String EMPTY_ROW_ID = "empty";

    private Boolean requestingFunding;

    private Integer grantClaimPercentage;

    private Boolean otherFunding;

    private Map<String, OtherFundingRowForm> otherFundingRows;

    private Boolean termsAgreed;

    private long otherFundingQuestionId;

    public Boolean getRequestingFunding() {
        return requestingFunding;
    }

    public void setRequestingFunding(Boolean requestingFunding) {
        this.requestingFunding = requestingFunding;
    }

    public Integer getGrantClaimPercentage() {
        return grantClaimPercentage;
    }

    public void setGrantClaimPercentage(Integer grantClaimPercentage) {
        this.grantClaimPercentage = grantClaimPercentage;
    }

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

    public Boolean getTermsAgreed() {
        return termsAgreed;
    }

    public void setTermsAgreed(Boolean termsAgreed) {
        this.termsAgreed = termsAgreed;
    }

    public long getOtherFundingQuestionId() {
        return otherFundingQuestionId;
    }

    public void setOtherFundingQuestionId(long otherFundingQuestionId) {
        this.otherFundingQuestionId = otherFundingQuestionId;
    }

    public BigDecimal getOtherFundingTotal() {
        return otherFundingRows == null ? BigDecimal.ZERO :
                otherFundingRows.entrySet().stream()
                        .map(Map.Entry::getValue)
                        .map(OtherFundingRowForm::getFundingAmount)
                        .filter(Objects::nonNull)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

}
