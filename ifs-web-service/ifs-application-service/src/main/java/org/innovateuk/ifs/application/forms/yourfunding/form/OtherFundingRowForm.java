package org.innovateuk.ifs.application.forms.yourfunding.form;

import org.innovateuk.ifs.commons.validation.constraints.FieldRequiredIf;

import javax.validation.constraints.Digits;
import javax.validation.constraints.Pattern;
import java.math.BigDecimal;

import static com.google.common.base.Strings.isNullOrEmpty;

@FieldRequiredIf(required = "source", argument = "blank", predicate = false, message = "{validation.finance.funding.source.blank}")
@FieldRequiredIf(required = "date", argument = "blank", predicate = false, message = "{validation.finance.secured.date.invalid}")
@FieldRequiredIf(required = "fundingAmount", argument = "blank", predicate = false, message = "{validation.finance.funding.amount.invalid}")
public class OtherFundingRowForm {

    private Long costId;

    private String source;

    @Pattern(regexp = "^(?:((0[1-9]|1[012])-[0-9]{4})|)$", message = "{validation.finance.secured.date.invalid}")
    private String date;

    @Digits(integer = 20, fraction = 0, message = "{validation.finance.funding.amount.invalid}")
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
        return isNullOrEmpty(source) && isNullOrEmpty(date) && fundingAmount == null && costId == null;
    }
}
