package org.innovateuk.ifs.application.forms.yourfunding.form;

import org.innovateuk.ifs.commons.validation.constraints.FieldRequiredIf;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Map;
import java.util.Objects;

@FieldRequiredIf(required = "grantClaimPercentage", argument = "requestingFunding", predicate = true, message = "{validation.field.must.not.be.blank}")
@FieldRequiredIf(required = "termsAgreed", argument = "complete", predicate = true, message = "{validation.field.must.not.be.blank}")
public class YourFundingForm {
    public static final String EMPTY_ROW_ID = "empty";
    @NotNull
    private Boolean requestingFunding;

    private Integer grantClaimPercentage;

    @NotNull
    private Boolean otherFunding;

    @Valid
    private Map<String, OtherFundingRowForm> otherFundingRows;

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
