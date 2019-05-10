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

    public FinanceContactViewModel(List<ProjectUserResource> orgUsers,
                                   long projectId,
                                   String projectName) {
        this.orgUsers = orgUsers;
        this.projectId = projectId;
        this.projectName = projectName;
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

}