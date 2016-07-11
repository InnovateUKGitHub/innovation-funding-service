package com.worth.ifs.project.viewmodel;

import com.worth.ifs.application.resource.ApplicationResource;
import com.worth.ifs.competition.resource.CompetitionResource;
import com.worth.ifs.project.resource.ProjectResource;

/**
 * A view model that backs the Project Status page
 */
public class ProjectStatusViewModel {

    private ProjectResource project;
    private ApplicationResource app;
    private CompetitionResource competition;

    public ProjectStatusViewModel(ProjectResource project, ApplicationResource app, CompetitionResource competition) {
        this.project = project;
        this.app = app;
        this.competition = competition;
    }

    public ProjectResource getProject() {
        return project;
    }

    public ApplicationResource getApp() {
        return app;
    }

    public CompetitionResource getCompetition() {
        return competition;
    }
}
