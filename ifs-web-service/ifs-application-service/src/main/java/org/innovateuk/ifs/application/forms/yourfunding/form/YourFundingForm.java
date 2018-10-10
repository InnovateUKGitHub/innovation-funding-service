package org.innovateuk.ifs.application.forms.yourfunding.form;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Objects;

public class YourFundingForm {

    private Boolean requestingFunding;

    private Integer fundingLevel;

    private Boolean otherFunding;

    private Map<Long, OtherFundingRowForm> otherFundingRows;

    private Boolean termsAgreed;

    private long grantClaimQuestionId;

    private long otherFundingQuestionId;

    private boolean complete;

    public Boolean getRequestingFunding() {
        return requestingFunding;
    }

    public void setRequestingFunding(Boolean requestingFunding) {
        this.requestingFunding = requestingFunding;
    }

    public Integer getFundingLevel() {
        return fundingLevel;
    }

    public void setFundingLevel(Integer fundingLevel) {
        this.fundingLevel = fundingLevel;
    }

    public Boolean getOtherFunding() {
        return otherFunding;
    }

    public void setOtherFunding(Boolean otherFunding) {
        this.otherFunding = otherFunding;
    }

    public Map<Long, OtherFundingRowForm> getOtherFundingRows() {
        return otherFundingRows;
    }

    public void setOtherFundingRows(Map<Long, OtherFundingRowForm> otherFundingRows) {
        this.otherFundingRows = otherFundingRows;
    }

    public Boolean getTermsAgreed() {
        return termsAgreed;
    }

    public void setTermsAgreed(Boolean termsAgreed) {
        this.termsAgreed = termsAgreed;
    }

    public long getGrantClaimQuestionId() {
        return grantClaimQuestionId;
    }

    public void setGrantClaimQuestionId(long grantClaimQuestionId) {
        this.grantClaimQuestionId = grantClaimQuestionId;
    }

    public long getOtherFundingQuestionId() {
        return otherFundingQuestionId;
    }

    public void setOtherFundingQuestionId(long otherFundingQuestionId) {
        this.otherFundingQuestionId = otherFundingQuestionId;
    }

    public boolean isComplete() {
        return complete;
    }

    public void setComplete(boolean complete) {
        this.complete = complete;
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
