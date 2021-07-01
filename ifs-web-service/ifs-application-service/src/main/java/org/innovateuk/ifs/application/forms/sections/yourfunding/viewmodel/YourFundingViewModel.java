package org.innovateuk.ifs.application.forms.sections.yourfunding.viewmodel;

import org.innovateuk.ifs.analytics.BaseAnalyticsViewModel;
import org.innovateuk.ifs.competition.publiccontent.resource.FundingType;
import org.innovateuk.ifs.organisation.resource.OrganisationTypeEnum;

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

    private final boolean subsidyBasisQuestionRequired;

    private final Long researchCategoryQuestionId;

    private final long yourOrganisationSectionId;

    private final Long subsidyBasisQuestionId;

    private final Integer maximumFundingLevel;

    private final String financesUrl;

    private final boolean overridingFundingRules;

    private final FundingType fundingType;

    private final OrganisationTypeEnum organisationType;

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
                                boolean subsidyBasisQuestionRequired,
                                Long researchCategoryQuestionId,
                                long yourOrganisationSectionId,
                                Long subsidyBasisQuestionId,
                                Integer maximumFundingLevel,
                                String financesUrl,
                                boolean overridingFundingRules,
                                FundingType fundingType,
                                OrganisationTypeEnum organisationType) {
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
        this.subsidyBasisQuestionRequired = subsidyBasisQuestionRequired;
        this.researchCategoryQuestionId = researchCategoryQuestionId;
        this.yourOrganisationSectionId = yourOrganisationSectionId;
        this.subsidyBasisQuestionId = subsidyBasisQuestionId;
        this.maximumFundingLevel = maximumFundingLevel;
        this.financesUrl = financesUrl;
        this.overridingFundingRules = overridingFundingRules;
        this.fundingType = fundingType;
        this.organisationType = organisationType;
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

    public boolean isSubsidyBasisQuestionRequired() {
        return subsidyBasisQuestionRequired;
    }

    public Long getResearchCategoryQuestionId() {
        return researchCategoryQuestionId;
    }

    public long getYourOrganisationSectionId() {
        return yourOrganisationSectionId;
    }

    public Long getSubsidyBasisQuestionId() {
        return subsidyBasisQuestionId;
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

    public FundingType getFundingType() {
        return fundingType;
    }

    public boolean isKtpFundingType() {
        return FundingType.KTP == fundingType;
    }

    public OrganisationTypeEnum getOrganisationType() {
        return organisationType;
    }

    /* view logic */
    public boolean isReadOnly() {
        return complete || !open;
    }

    /*
    * Will all lead applicants on a ktp competition be Knowledge Base
    *
    * */
    public boolean hideAreYouRequestingFunding() {
        return isKtpFundingType() && organisationType != OrganisationTypeEnum.KNOWLEDGE_BASE;
    }

    public String getPageTitle() {
        return isKtpFundingType() && organisationType != OrganisationTypeEnum.KNOWLEDGE_BASE
                ? "Other funding" : "Your funding";
    }
}