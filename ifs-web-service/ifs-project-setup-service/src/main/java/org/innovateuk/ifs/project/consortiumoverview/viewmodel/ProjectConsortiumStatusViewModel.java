package org.innovateuk.ifs.project.consortiumoverview.viewmodel;

import org.innovateuk.ifs.project.internal.ProjectSetupStage;
import org.innovateuk.ifs.project.status.resource.ProjectTeamStatusResource;

import java.util.List;
import java.util.Objects;

public class ProjectConsortiumStatusViewModel {
    private Long projectId;
    private ProjectTeamStatusResource projectTeamStatusResource;
    private List<ProjectSetupStage> stages;

    public ProjectConsortiumStatusViewModel(Long projectId, ProjectTeamStatusResource projectTeamStatusResource, List<ProjectSetupStage> stages) {
        this.projectId = projectId;
        this.projectTeamStatusResource = projectTeamStatusResource;
        this.stages = stages;
    }

    public Long getProjectId() {
        return projectId;
    }

    public ProjectTeamStatusResource getProjectTeamStatusResource() {
        return projectTeamStatusResource;
    }

    public List<ProjectSetupStage> getStages() {
        return stages;
    }

    public boolean hasProjectDetails() {
        return stages.contains(ProjectSetupStage.PROJECT_DETAILS);
    }

    public boolean hasProjectTeam() {
        return stages.contains(ProjectSetupStage.PROJECT_TEAM);
    }

    public boolean hasDocuments() {
        return stages.contains(ProjectSetupStage.DOCUMENTS);
    }

    public boolean hasMonitoringOfficer() {
        return stages.contains(ProjectSetupStage.MONITORING_OFFICER);
    }

    public boolean hasBankDetails() {
        return stages.contains(ProjectSetupStage.BANK_DETAILS);
    }

    public boolean hasFinanceChecks() {
        return stages.contains(ProjectSetupStage.FINANCE_CHECKS);
    }

    public boolean hasSpendProfile() {
        return stages.contains(ProjectSetupStage.SPEND_PROFILE);
    }

    public boolean hasGrantOfferLetter() {
        return stages.contains(ProjectSetupStage.GRANT_OFFER_LETTER);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProjectConsortiumStatusViewModel that = (ProjectConsortiumStatusViewModel) o;
        return Objects.equals(projectId, that.projectId) &&
                Objects.equals(projectTeamStatusResource, that.projectTeamStatusResource) &&
                Objects.equals(stages, that.stages);
    }

    @Override
    public int hashCode() {
        return Objects.hash(projectId, projectTeamStatusResource, stages);
    }
}
