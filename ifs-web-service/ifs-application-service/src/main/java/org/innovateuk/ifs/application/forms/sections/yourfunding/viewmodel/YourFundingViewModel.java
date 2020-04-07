package org.innovateuk.ifs.application.forms.sections.yourfunding.viewmodel;

import org.innovateuk.ifs.analytics.BaseAnalyticsViewModel;

public class YourFundingViewModel implements BaseAnalyticsViewModel {
    private final long applicationId;

    private final String competitionName;

    private final long sectionId;

    private final long organisationId;

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

    private final boolean overridingFundingRules;

    public YourFundingViewModel(long applicationId,
                                String competitionName,
                                long sectionId,
                                long organisationId,
                                long competitionId,
                                boolean complete,
                                boolean open,
                                boolean leadApplicant,
                                boolean business,
                                String applicationName,
                                boolean fundingSectionLocked,
                                boolean researchCategoryRequired,
                                boolean yourOrganisationRequired,
                                Long researchCategoryQuestionId,
                                long yourOrganisationSectionId,
                                Integer maximumFundingLevel,
                                String financesUrl,
                                boolean overridingFundingRules) {
        this.applicationId = applicationId;
        this.competitionName = competitionName;
        this.sectionId = sectionId;
        this.organisationId = organisationId;
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
        this.overridingFundingRules = overridingFundingRules;
    }

    @Override
    public Long getApplicationId() {
        return applicationId;
    }

    @Override
    public String getCompetitionName() {
        return competitionName;
    }

    public long getSectionId() {
        return sectionId;
    }

    public long getOrganisationId() {
        return organisationId;
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

    public boolean isOverridingFundingRules() {
        return overridingFundingRules;
    }

    /* view logic */
    public boolean isReadOnly() {
        return complete || !open;
    }
}