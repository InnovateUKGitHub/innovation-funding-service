package org.innovateuk.ifs.application.forms.yourfunding.viewmodel;

public class YourFundingViewModel {
    private final long applicationId;

    private final long competitionId;

    private final boolean complete;

    private final boolean leadApplicant;

    private final boolean business;

    private final String applicationName;

    private final boolean fundingSectionLocked;

    private final boolean researchCategoryRequired;

    private final boolean yourOrganisationRequired;

    private final Long researchCategoryQuestionId;

    private final long yourOrganisationSectionId;

    private final Integer maximumFundingLevel;

    public YourFundingViewModel(long applicationId, long competitionId, boolean complete, boolean leadApplicant, boolean business, String applicationName, boolean fundingSectionLocked, boolean researchCategoryRequired, boolean yourOrganisationRequired, Long researchCategoryQuestionId, long yourOrganisationSectionId, Integer maximumFundingLevel) {
        this.applicationId = applicationId;
        this.competitionId = competitionId;
        this.complete = complete;
        this.leadApplicant = leadApplicant;
        this.business = business;
        this.applicationName = applicationName;
        this.fundingSectionLocked = fundingSectionLocked;
        this.researchCategoryRequired = researchCategoryRequired;
        this.yourOrganisationRequired = yourOrganisationRequired;
        this.researchCategoryQuestionId = researchCategoryQuestionId;
        this.yourOrganisationSectionId = yourOrganisationSectionId;
        this.maximumFundingLevel = maximumFundingLevel;
    }

    public long getApplicationId() {
        return applicationId;
    }

    public long getCompetitionId() {
        return competitionId;
    }

    public boolean isComplete() {
        return complete;
    }

    public boolean isLeadApplicant() {
        return leadApplicant;
    }

    public boolean isBusiness() {
        return business;
    }

    public String getApplicationName() {
        return applicationName;
    }

    public boolean isFundingSectionLocked() {
        return fundingSectionLocked;
    }

    public boolean isResearchCategoryRequired() {
        return researchCategoryRequired;
    }

    public boolean isYourOrganisationRequired() {
        return yourOrganisationRequired;
    }

    public Long getResearchCategoryQuestionId() {
        return researchCategoryQuestionId;
    }

    public long getYourOrganisationSectionId() {
        return yourOrganisationSectionId;
    }

    public Integer getMaximumFundingLevel() {
        return maximumFundingLevel;
    }
}
