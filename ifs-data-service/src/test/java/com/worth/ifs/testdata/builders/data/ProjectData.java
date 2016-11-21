package com.worth.ifs.testdata.builders.data;

import com.worth.ifs.application.resource.ApplicationResource;
import com.worth.ifs.project.resource.ProjectResource;
import com.worth.ifs.user.resource.UserResource;

/**
 * TODO DW - document this class
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
