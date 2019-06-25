package org.innovateuk.ifs.project.financereviewer.viewmodel;

import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.user.resource.SimpleUserResource;

import java.util.List;

public class FinanceReviewerViewModel {

    private final long projectId;
    private final String projectName;
    private final long competitionId;
    private final List<SimpleUserResource> users;

    public FinanceReviewerViewModel(ProjectResource project, List<SimpleUserResource> users) {
        this.projectId = project.getId();
        this.projectName = project.getName();
        this.competitionId = project.getCompetition();
        this.users = users;
    }

    public long getProjectId() {
        return projectId;
    }

    public String getProjectName() {
        return projectName;
    }

    public long getCompetitionId() {
        return competitionId;
    }

    public List<SimpleUserResource> getUsers() {
        return users;
    }
}
