package org.innovateuk.ifs.project.projectteam.viewmodel;

import org.innovateuk.ifs.project.resource.ProjectUserResource;

import java.util.List;

/**
 * View model for the Project Manager page
 */
public class ProjectManagerViewModel {

    private final List<ProjectUserResource> leadOrgUsers;
    private final long projectId;
    private final String projectName;
    private final boolean loanCompetition;

    public ProjectManagerViewModel(List<ProjectUserResource> leadOrgUsers,
                                   long projectId,
                                   String projectName,
                                   boolean loanCompetition) {
        this.leadOrgUsers = leadOrgUsers;
        this.projectId = projectId;
        this.projectName = projectName;
        this.loanCompetition = loanCompetition;
    }

    public List<ProjectUserResource> getLeadOrgUsers() {
        return leadOrgUsers;
    }

    public long getProjectId() {
        return projectId;
    }

    public String getProjectName() {
        return projectName;
    }

    public boolean isLoanCompetition() {
        return loanCompetition;
    }
}
