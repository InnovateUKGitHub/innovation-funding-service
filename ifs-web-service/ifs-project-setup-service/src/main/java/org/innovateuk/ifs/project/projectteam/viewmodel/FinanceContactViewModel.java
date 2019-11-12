package org.innovateuk.ifs.project.projectteam.viewmodel;

import org.innovateuk.ifs.project.resource.ProjectUserResource;

import java.util.List;

/**
 * View model for the Finance Contact page
 */
public class FinanceContactViewModel {

    private final List<ProjectUserResource> orgUsers;
    private final long projectId;
    private final String projectName;
    private final boolean loanCompetition;

    public FinanceContactViewModel(List<ProjectUserResource> orgUsers,
                                   long projectId,
                                   String projectName,
                                   boolean loanCompetition) {
        this.orgUsers = orgUsers;
        this.projectId = projectId;
        this.projectName = projectName;
        this.loanCompetition = loanCompetition;
    }

    public List<ProjectUserResource> getOrgUsers() {
        return orgUsers;
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