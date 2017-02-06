package org.innovateuk.ifs.project.financechecks.viewmodel;

import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.thread.viewmodel.ThreadViewModel;
import org.innovateuk.ifs.user.resource.OrganisationResource;

import java.util.List;

/**
 * View model to back the Finance Checks page.
 */
public class ProjectFinanceChecksViewModel {
    private Long projectId;
    private Long organisationId;
    private String projectName;
    private List<ThreadViewModel> queries;
    private boolean approved;

    public ProjectFinanceChecksViewModel(ProjectResource project, OrganisationResource organisation,
                                         List<ThreadViewModel> queries, boolean approved) {
        this.projectId = project.getId();
        this.organisationId = organisation.getId();
        this.projectName = project.getName();
        this.queries = queries;
        this.approved = approved;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public Long getOrganisationId() {
        return organisationId;
    }

    public void setOrganisationId(Long organisationId) {
        this.organisationId = organisationId;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public List<ThreadViewModel> getQueries() {
        return queries;
    }

    public void setQueries(List<ThreadViewModel> queries) {
        this.queries = queries;
    }

    public boolean isApproved() {
        return approved;
    }

    public void setApproved(boolean approved) {
        this.approved = approved;
    }
}
