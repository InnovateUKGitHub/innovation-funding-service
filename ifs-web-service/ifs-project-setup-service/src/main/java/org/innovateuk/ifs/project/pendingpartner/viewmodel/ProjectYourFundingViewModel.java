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
                                       Optional<Long> subsidyBasisQuestionId) {
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
        return FundingType.KTP == fundingType;
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
}