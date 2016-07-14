package com.worth.ifs.project.otherdocuments.viewmodel;

import com.worth.ifs.project.viewmodel.BasicProjectDetailsViewModel;

/**
 * View model backing the Other Documents page
 */
public class ProjectOtherDocumentsViewModel implements BasicProjectDetailsViewModel {

    private Long projectId;
    private String projectName;

    public ProjectOtherDocumentsViewModel(Long projectId, String projectName) {
        this.projectId = projectId;
        this.projectName = projectName;
    }

    public Long getProjectId() {
        return projectId;
    }

    @Override
    public String getProjectName() {
        return projectName;
    }
}
