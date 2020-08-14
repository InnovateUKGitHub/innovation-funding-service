package org.innovateuk.ifs.project.pendingpartner.viewmodel;

import org.innovateuk.ifs.competition.publiccontent.resource.FundingType;
import org.innovateuk.ifs.organisation.resource.OrganisationTypeEnum;
import org.innovateuk.ifs.project.resource.ProjectResource;

public class ProjectYourFundingViewModel {

    private final String projectName;
    private final long projectId;
    private final long organisationId;
    private final boolean readOnly;
    private final OrganisationTypeEnum organisationType;
    private final int maximumFundingLevel;
    private final boolean fundingSectionLocked;
    private final long competitionId;
    private final boolean overridingFundingRules;
    private final FundingType fundingType;

    public ProjectYourFundingViewModel(ProjectResource project,
                                       long organisationId,
                                       boolean readOnly,
                                       int maximumFundingLevel,
                                       boolean fundingSectionLocked,
                                       long competitionId,
                                       boolean overridingFundingRules,
                                       FundingType fundingType,
                                       OrganisationTypeEnum organisationType) {
        this.projectName = project.getName();
        this.projectId = project.getId();
        this.organisationId = organisationId;
        this.readOnly = readOnly;
        this.organisationType = organisationType;
        this.maximumFundingLevel = maximumFundingLevel;
        this.fundingSectionLocked = fundingSectionLocked;
        this.competitionId = competitionId;
        this.overridingFundingRules = overridingFundingRules;
        this.fundingType = fundingType;
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
        return fundingSectionLocked;
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

}