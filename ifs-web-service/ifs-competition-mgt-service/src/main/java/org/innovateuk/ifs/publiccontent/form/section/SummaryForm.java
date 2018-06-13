package org.innovateuk.ifs.publiccontent.form.section;

import org.hibernate.validator.constraints.NotBlank;
import org.innovateuk.ifs.publiccontent.form.AbstractContentGroupForm;

import javax.validation.Valid;
import javax.validation.constraints.Size;

/**
 * Form for the Summary page on public content setup.
 */
public class SummaryForm extends AbstractContentGroupForm {

    @NotBlank (message="{validation.publiccontent.summaryform.description.required}")
    private String description;
    @NotBlank (message="{validation.publiccontent.summaryform.fundingType.required}")
    private String fundingType;
    @Valid
    @NotBlank (message="{validation.publiccontent.summaryform.projectSize.required}")
    @Size(max = 255, message = "{validation.publiccontent.summaryform.projectSize.max}")
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
