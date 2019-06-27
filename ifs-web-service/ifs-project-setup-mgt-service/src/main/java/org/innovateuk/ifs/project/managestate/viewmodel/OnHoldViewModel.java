package org.innovateuk.ifs.project.managestate.viewmodel;

import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.threads.resource.ProjectStateCommentsResource;

import java.util.List;

import static java.util.stream.Collectors.toList;

public class OnHoldViewModel {

    private final long competitionId;
    private final long applicationId;
    private final long projectId;
    private final String projectName;

    private final String title;
    private final List<ProjectStateCommentViewModel> comments;



    public OnHoldViewModel(ProjectResource project, ProjectStateCommentsResource commentsResource) {
        this.competitionId = project.getCompetition();
        this.applicationId = project.getApplication();
        this.projectId = project.getId();
        this.projectName = project.getName();
        this.title = commentsResource.title;
        this.comments = commentsResource.posts.stream()
                .map(ProjectStateCommentViewModel::new)
                .collect(toList());
    }

    public long getCompetitionId() {
        return competitionId;
    }

    public long getApplicationId() {
        return applicationId;
    }

    public long getProjectId() {
        return projectId;
    }

    public String getProjectName() {
        return projectName;
    }

    public String getTitle() {
        return title;
    }

    public List<ProjectStateCommentViewModel> getComments() {
        return comments;
    }
}
