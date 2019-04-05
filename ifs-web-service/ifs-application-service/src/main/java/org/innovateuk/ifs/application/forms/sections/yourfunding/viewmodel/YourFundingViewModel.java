package org.innovateuk.ifs.application.forms.sections.yourfunding.viewmodel;

public class YourFundingViewModel {
    private final long applicationId;

    private final long sectionId;

    private final long competitionId;

    private final boolean complete;

    private final boolean open;

    private final boolean leadApplicant;

    private final boolean business;

    private final String applicationName;

    private final boolean fundingSectionLocked;

    private final boolean researchCategoryRequired;

    private final boolean yourOrganisationRequired;

    private final Long researchCategoryQuestionId;

    private final long yourOrganisationSectionId;

    private final Integer maximumFundingLevel;

    private final String financesUrl;

    public YourFundingViewModel(long applicationId, long sectionId, long competitionId, boolean complete, boolean open, boolean leadApplicant, boolean business, String applicationName, boolean fundingSectionLocked, boolean researchCategoryRequired, boolean yourOrganisationRequired, Long researchCategoryQuestionId, long yourOrganisationSectionId, Integer maximumFundingLevel, String financesUrl) {
        this.applicationId = applicationId;
        this.sectionId = sectionId;
        this.competitionId = competitionId;
        this.complete = complete;
        this.open = open;
        this.leadApplicant = leadApplicant;
        this.business = business;
        this.applicationName = applicationName;
        this.fundingSectionLocked = fundingSectionLocked;
        this.researchCategoryRequired = researchCategoryRequired;
        this.yourOrganisationRequired = yourOrganisationRequired;
        this.researchCategoryQuestionId = researchCategoryQuestionId;
        this.yourOrganisationSectionId = yourOrganisationSectionId;
        this.maximumFundingLevel = maximumFundingLevel;
        this.financesUrl = financesUrl;
    }

    public long getApplicationId() {
        return applicationId;
    }

    public long getSectionId() {
        return sectionId;
    }

    public long getCompetitionId() {
        return competitionId;
    }

    public boolean isComplete() {
        return complete;
    }

    public boolean isOpen() {
        return open;
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

    public String getFinancesUrl() {
        return financesUrl;
    }

    /* view logic */
    public boolean isReadOnly() {
        return complete || !open;
    }
}
