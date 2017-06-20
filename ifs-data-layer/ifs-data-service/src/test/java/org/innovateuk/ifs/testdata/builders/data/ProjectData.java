package org.innovateuk.ifs.testdata.builders.data;

import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.user.resource.UserResource;

/**
 * Provides a running data context for generating Project data
 */
public class ProjectData {
    private ApplicationResource application;
    private UserResource leadApplicant;
    private ProjectResource project;
    private UserResource projectManager;

    public void setApplication(ApplicationResource application) {
        this.application = application;
    }

    public ApplicationResource getApplication() {
        return application;
    }


    public void setLeadApplicant(UserResource leadApplicant) {
        this.leadApplicant = leadApplicant;
    }

    public UserResource getLeadApplicant() {
        return leadApplicant;
    }

    public void setProject(ProjectResource project) {
        this.project = project;
    }

    public ProjectResource getProject() {
        return project;
    }

    public void setProjectManager(UserResource projectManager) {
        this.projectManager = projectManager;
    }

    public UserResource getProjectManager() {
        return projectManager;
    }
}
