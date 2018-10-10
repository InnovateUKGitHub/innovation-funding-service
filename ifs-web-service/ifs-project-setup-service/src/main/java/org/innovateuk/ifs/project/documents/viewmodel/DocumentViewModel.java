package org.innovateuk.ifs.project.documents.viewmodel;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.innovateuk.ifs.file.controller.viewmodel.FileDetailsViewModel;
import org.innovateuk.ifs.project.document.resource.DocumentStatus;

public class DocumentViewModel {

    private Long projectId;
    private String projectName;
    private String title;
    private FileDetailsViewModel fileDetails;
    private DocumentStatus status;
    private boolean projectManager;

    public DocumentViewModel(Long projectId, String projectName, String title,
                             FileDetailsViewModel fileDetails, DocumentStatus status, boolean projectManager) {
        this.projectId = projectId;
        this.projectName = projectName;
        this.title = title;
        this.fileDetails = fileDetails;
        this.status = status;
        this.projectManager = projectManager;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public DocumentStatus getStatus() {
        return status;
    }

    public void setStatus(DocumentStatus status) {
        this.status = status;
    }

    public FileDetailsViewModel getFileDetails() {
        return fileDetails;
    }

    public void setFileDetails(FileDetailsViewModel fileDetails) {
        this.fileDetails = fileDetails;
    }

    public boolean isProjectManager() {
        return projectManager;
    }

    public void setProjectManager(boolean projectManager) {
        this.projectManager = projectManager;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        DocumentViewModel that = (DocumentViewModel) o;

        return new EqualsBuilder()
                .append(projectManager, that.projectManager)
                .append(projectId, that.projectId)
                .append(projectName, that.projectName)
                .append(title, that.title)
                .append(status, that.status)
                .append(fileDetails, that.fileDetails)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(projectId)
                .append(projectName)
                .append(title)
                .append(status)
                .append(fileDetails)
                .append(projectManager)
                .toHashCode();
    }
}
