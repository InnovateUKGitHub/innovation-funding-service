package org.innovateuk.ifs.project.documents.viewmodel;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.innovateuk.ifs.file.controller.viewmodel.FileDetailsViewModel;
import org.innovateuk.ifs.project.document.resource.DocumentStatus;

/**
 * View model for viewing/actions on each document
 */
public class DocumentViewModel {

    private Long projectId;
    private String projectName;
    private Long documentConfigId;
    private String title;
    private FileDetailsViewModel fileDetails;
    private DocumentStatus status;

    public DocumentViewModel(Long projectId, String projectName, Long documentConfigId, String title, FileDetailsViewModel fileDetails, DocumentStatus status) {
        this.projectId = projectId;
        this.projectName = projectName;
        this.documentConfigId = documentConfigId;
        this.title = title;
        this.fileDetails = fileDetails;
        this.status = status;
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

    public Long getDocumentConfigId() {
        return documentConfigId;
    }

    public void setDocumentConfigId(Long documentConfigId) {
        this.documentConfigId = documentConfigId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public FileDetailsViewModel getFileDetails() {
        return fileDetails;
    }

    public void setFileDetails(FileDetailsViewModel fileDetails) {
        this.fileDetails = fileDetails;
    }

    public DocumentStatus getStatus() {
        return status;
    }

    public void setStatus(DocumentStatus status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        DocumentViewModel that = (DocumentViewModel) o;

        return new EqualsBuilder()
                .append(projectId, that.projectId)
                .append(projectName, that.projectName)
                .append(documentConfigId, that.documentConfigId)
                .append(title, that.title)
                .append(fileDetails, that.fileDetails)
                .append(status, that.status)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(projectId)
                .append(projectName)
                .append(documentConfigId)
                .append(title)
                .append(fileDetails)
                .append(status)
                .toHashCode();
    }
}
