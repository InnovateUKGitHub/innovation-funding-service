package com.worth.ifs.project.otherdocuments.viewmodel;

/**
 * View model backing the Other Documents page
 */
public class ProjectOtherDocumentsViewModel {

    private Long projectId;

    public ProjectOtherDocumentsViewModel(Long projectId) {
        this.projectId = projectId;
    }

    public Long getProjectId() {
        return projectId;
    }
}
