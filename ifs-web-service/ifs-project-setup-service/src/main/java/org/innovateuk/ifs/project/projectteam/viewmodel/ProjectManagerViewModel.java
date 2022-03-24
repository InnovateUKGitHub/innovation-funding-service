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
    private final boolean ktpCompetition;
    private final boolean hecpCompetition;

    public ProjectManagerViewModel(List<ProjectUserResource> leadOrgUsers,
                                   long projectId,
                                   String projectName,
                                   boolean loanCompetition,
                                   boolean ktpCompetition,
                                   boolean hecpCompetition) {
        this.leadOrgUsers = leadOrgUsers;
        this.projectId = projectId;
        this.projectName = projectName;
        this.loanCompetition = loanCompetition;
        this.ktpCompetition = ktpCompetition;
        this.hecpCompetition = hecpCompetition;
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

    public boolean isKtpCompetition() {
        return ktpCompetition;
    }

    public boolean isHecpCompetition() {
        return hecpCompetition;
    }
}
