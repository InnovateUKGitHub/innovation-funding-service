package org.innovateuk.ifs.publiccontent.form;

import org.hibernate.validator.constraints.NotEmpty;

/**
 * Form for the Search page on public content setup.
 */
public class SearchForm extends AbstractPublicContentForm {

    @NotEmpty
    private String shortDescription;
    @NotEmpty
    private String projectFundingRange;
    @NotEmpty
    private String eligibilitySummary;
    @NotEmpty
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
