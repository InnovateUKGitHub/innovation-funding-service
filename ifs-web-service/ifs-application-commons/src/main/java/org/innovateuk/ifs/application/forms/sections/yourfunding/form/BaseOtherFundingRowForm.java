package org.innovateuk.ifs.application.forms.sections.yourfunding.form;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.innovateuk.ifs.application.forms.sections.yourprojectcosts.form.AbstractCostRowForm;
import org.innovateuk.ifs.finance.resource.EmployeesAndTurnoverResource;
import org.innovateuk.ifs.finance.resource.GrowthTableResource;
import org.innovateuk.ifs.finance.resource.KtpYearsResource;
import org.innovateuk.ifs.finance.resource.cost.BaseOtherFunding;
import org.innovateuk.ifs.finance.resource.cost.PreviousFunding;

import java.math.BigDecimal;

import static com.google.common.base.Strings.isNullOrEmpty;

//@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
//@JsonSubTypes({
//        @JsonSubTypes.Type(value = PreviousFundingRowForm.class, name = "PreviousFundingRowForm"),
//        @JsonSubTypes.Type(value = OtherFundingRowForm.class, name = "OtherFundingRowForm")
//})
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
