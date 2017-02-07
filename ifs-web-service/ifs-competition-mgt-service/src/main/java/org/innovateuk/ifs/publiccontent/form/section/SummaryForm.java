package org.innovateuk.ifs.publiccontent.form.section;

import org.hibernate.validator.constraints.NotEmpty;
import org.innovateuk.ifs.publiccontent.form.AbstractContentGroupForm;

/**
 * Form for the Summary page on public content setup.
 */
public class SummaryForm extends AbstractContentGroupForm {

    @NotEmpty (message="{validation.publiccontent.summaryform.description.required}")
    private String description;
    @NotEmpty (message="{validation.publiccontent.summaryform.fundingType.SummaryFormrequired}")
    private String fundingType;
    @NotEmpty (message="{validation.publiccontent.summaryform.projectSize.required}")
    private String projectSize;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getFundingType() {
        return fundingType;
    }

    public void setFundingType(String fundingType) {
        this.fundingType = fundingType;
    }

    public String getProjectSize() {
        return projectSize;
    }

    public void setProjectSize(String projectSize) {
        this.projectSize = projectSize;
    }

}
