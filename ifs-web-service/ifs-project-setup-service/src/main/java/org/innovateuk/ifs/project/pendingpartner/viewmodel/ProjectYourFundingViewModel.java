package org.innovateuk.ifs.project.pendingpartner.viewmodel;

import org.innovateuk.ifs.competition.publiccontent.resource.FundingType;
import org.innovateuk.ifs.organisation.resource.OrganisationTypeEnum;
import org.innovateuk.ifs.project.resource.ProjectResource;

import java.util.Optional;

public class ProjectYourFundingViewModel {

    private final String projectName;
    private final long projectId;
    private final long organisationId;
    private final boolean readOnly;
    private final OrganisationTypeEnum organisationType;
    private final int maximumFundingLevel;
    private final long competitionId;
    private final boolean overridingFundingRules;
    private final FundingType fundingType;
    private final boolean leadOrganisation;
    private final boolean organisationRequiredAndNotCompleted;
    private final boolean subsidyBasisRequiredAndNotCompleted;
    private final Optional<Long> subsidyBasisQuestionId;
    private final boolean isThirdPartyOfgem;
    private String hash;
    private final boolean isFixedFundingLevel;
    private final boolean isCompTypeOfgemAndFundingTypeThirdParty;
    private final boolean isThirdPartyFundingType;

    public ProjectYourFundingViewModel(ProjectResource project,
                                       long organisationId,
                                       boolean readOnly,
                                       int maximumFundingLevel,
                                       long competitionId,
                                       boolean overridingFundingRules,
                                       FundingType fundingType,
                                       OrganisationTypeEnum organisationType,
                                       boolean leadOrganisation,
                                       boolean subsidyBasisRequiredAndNotCompleted,
                                       boolean organisationRequiredAndNotCompleted,
                                       Optional<Long> subsidyBasisQuestionId,
                                       boolean isThirdPartyOfgem,
                                       String hash,
                                       boolean isFixedFundingLevel,
                                       boolean isCompTypeOfgemAndFundingTypeThirdParty,
                                       boolean isThirdPartyFundingType) {
        this.projectName = project.getName();
        this.projectId = project.getId();
        this.organisationId = organisationId;
        this.readOnly = readOnly;
        this.organisationType = organisationType;
        this.maximumFundingLevel = maximumFundingLevel;
        this.competitionId = competitionId;
        this.overridingFundingRules = overridingFundingRules;
        this.fundingType = fundingType;
        this.leadOrganisation = leadOrganisation;
        this.subsidyBasisRequiredAndNotCompleted = subsidyBasisRequiredAndNotCompleted;
        this.organisationRequiredAndNotCompleted = organisationRequiredAndNotCompleted;
        this.subsidyBasisQuestionId = subsidyBasisQuestionId;
        this.isThirdPartyOfgem = isThirdPartyOfgem;
        this.hash = hash;
        this.isFixedFundingLevel = isFixedFundingLevel;
        this.isCompTypeOfgemAndFundingTypeThirdParty = isCompTypeOfgemAndFundingTypeThirdParty;
        this.isThirdPartyFundingType = isThirdPartyFundingType;
    }

    public String getProjectName() {
        return projectName;
    }

    public long getProjectId() {
        return projectId;
    }

    public long getOrganisationId() {
        return organisationId;
    }

    public boolean isReadOnly() {
        return readOnly;
    }

    public int getMaximumFundingLevel() {
        return maximumFundingLevel;
    }

    public boolean isFundingSectionLocked() {
        return subsidyBasisRequiredAndNotCompleted || organisationRequiredAndNotCompleted;
    }

    public long getCompetitionId() {
        return competitionId;
    }

    public boolean isOverridingFundingRules() {
        return overridingFundingRules;
    }

    public FundingType getFundingType() {
        return fundingType;
    }

    public boolean isKtpFundingType() {
        return FundingType.KTP == fundingType
                || FundingType.KTP_AKT == fundingType;
    }

    public OrganisationTypeEnum getOrganisationType() {
        return organisationType;
    }

    public boolean isBusiness() {
        return OrganisationTypeEnum.BUSINESS == organisationType;
    }

    /* view logic */
    /*
     * Will all lead applicants on a ktp competition be Knowledge Base
     *
     * */
    public boolean hideAreYouRequestingFunding() {
        return isKtpFundingType() && organisationType != OrganisationTypeEnum.KNOWLEDGE_BASE;
    }

    public boolean isLeadOrganisation() {
        return leadOrganisation;
    }

    public boolean isOrganisationRequiredAndNotCompleted() {
        return organisationRequiredAndNotCompleted;
    }

    public boolean isSubsidyBasisRequiredAndNotCompleted() {
        return subsidyBasisRequiredAndNotCompleted;
    }

    public Long getSubsidyBasisQuestionId() {
        return subsidyBasisQuestionId.orElse(null);
    }

    public boolean isThirdPartyOfgem() {
        return isThirdPartyOfgem;
    }

    public String getHash() {
        return hash;
    }

    public boolean isFixedFundingLevel() {
        return isFixedFundingLevel;
    }

    public boolean isCompTypeOfgemAndFundingTypeThirdParty() {
        return isCompTypeOfgemAndFundingTypeThirdParty;
    }

    public boolean isThirdPartyFundingType() {
        return isThirdPartyFundingType;
    }

    public String getFundingHeading() {
        return isCompTypeOfgemAndFundingTypeThirdParty ? "Contributions in kind" : "Other funding";
    }

    public String getHeadingHint() {
        return isCompTypeOfgemAndFundingTypeThirdParty ? "Are you making any contributions in kind for this project?" :
                (isThirdPartyOfgem && !isThirdPartyFundingType) ? "Have you received any aligned or third party funding for this project?" :
                        "Have you received any other public sector funding for this project? This is important as other public sector support counts as part of the funding you can receive.";
    }

    public String getNoOtherFundingText() {
        return isCompTypeOfgemAndFundingTypeThirdParty ? "No contributions in kind" : "No other funding";
    }

    public String getSourceOfFundingText() {
        return isCompTypeOfgemAndFundingTypeThirdParty ? "Description and breakdown of contributions in kind" : "Source of funding";
    }

    public String getFundingAmountText() {
        return isCompTypeOfgemAndFundingTypeThirdParty ? "Contributions in kind value" : "Funding amount";
    }

    public String getFundingAmountWidth() {
        return isCompTypeOfgemAndFundingTypeThirdParty ? "width-250 govuk-table__header alignright" :"width-150 govuk-table__header";
    }

    public String getTableClass() {
        return isCompTypeOfgemAndFundingTypeThirdParty ? "govuk-table__cell" : "govuk-table__header";
    }

    public String getTotalText() {
        return isCompTypeOfgemAndFundingTypeThirdParty ? "Total contributions in kind" : "Total other funding";
    }

    public String addAnotherSourceText() {
        return isCompTypeOfgemAndFundingTypeThirdParty ? "Add another contribution in kind" : "Add another source of funding";
    }
}