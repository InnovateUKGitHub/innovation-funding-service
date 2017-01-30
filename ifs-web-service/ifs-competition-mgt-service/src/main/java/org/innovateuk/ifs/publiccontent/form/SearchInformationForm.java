package org.innovateuk.ifs.publiccontent.form;

import org.hibernate.validator.constraints.NotEmpty;

/**
 * Form for the Search page on public content setup.
 */
public class SearchInformationForm extends AbstractPublicContentForm {

    @NotEmpty (message="{validation.publiccontent.searchinformationform.shortDescription.required}")
    private String shortDescription;
    @NotEmpty (message="{validation.publiccontent.searchinformationform.projectFundingRange.required}")
    private String projectFundingRange;
    @NotEmpty (message="{validation.publiccontent.searchinformationform.eligibilitySummary.required}")
    private String eligibilitySummary;
    @NotEmpty (message="{validation.publiccontent.searchinformationform.keywords.required}")
    private String keywords;

    public String getShortDescription() {
        return shortDescription;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    public String getProjectFundingRange() {
        return projectFundingRange;
    }

    public void setProjectFundingRange(String projectFundingRange) {
        this.projectFundingRange = projectFundingRange;
    }

    public String getEligibilitySummary() {
        return eligibilitySummary;
    }

    public void setEligibilitySummary(String eligibilitySummary) {
        this.eligibilitySummary = eligibilitySummary;
    }

    public String getKeywords() {
        return keywords;
    }

    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }
}
