package org.innovateuk.ifs.project.pendingpartner.viewmodel;

import org.innovateuk.ifs.project.resource.ProjectResource;

public class ProjectYourFundingViewModel {

    private final String projectName;
    private final long projectId;
    private final long organisationId;
    private final boolean readOnly;
    private final boolean business;
    private final int maximumFundingLevel;
    private final boolean fundingSectionLocked;
    private final long competitionId;
    private final boolean overridingFundingRules;

    public ProjectYourFundingViewModel(ProjectResource project,
                                       long organisationId,
                                       boolean readOnly,
                                       boolean business,
                                       int maximumFundingLevel,
                                       boolean fundingSectionLocked,
                                       long competitionId,
                                       boolean overridingFundingRules) {
        this.projectName = project.getName();
        this.projectId = project.getId();
        this.organisationId = organisationId;
        this.readOnly = readOnly;
        this.business = business;
        this.maximumFundingLevel = maximumFundingLevel;
        this.fundingSectionLocked = fundingSectionLocked;
        this.competitionId = competitionId;
        this.overridingFundingRules = overridingFundingRules;
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

    public boolean isBusiness() {
        return business;
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

}