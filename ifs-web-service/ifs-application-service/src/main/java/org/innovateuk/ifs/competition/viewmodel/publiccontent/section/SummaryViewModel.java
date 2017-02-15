package org.innovateuk.ifs.competition.viewmodel.publiccontent.section;

import org.innovateuk.ifs.competition.viewmodel.publiccontent.AbstractPublicContentGroupViewModel;


/**
 * View model for the Summary section.
 */
public class SummaryViewModel extends AbstractPublicContentGroupViewModel {
    private String description;
    private String fundingType;
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
