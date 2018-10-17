package org.innovateuk.ifs.project.documents.viewmodel;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.innovateuk.ifs.project.document.resource.ProjectDocumentStatus;

import java.util.List;

public class AllDocumentsViewModel {

    private Long projectId;
    private String projectName;
    private List<ProjectDocumentStatus> documents;
    private boolean projectManager;

    public AllDocumentsViewModel(Long projectId, String projectName, List<ProjectDocumentStatus> documents, boolean projectManager) {
        this.projectId = projectId;
        this.projectName = projectName;
        this.documents = documents;
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

    public List<ProjectDocumentStatus> getDocuments() {
        return documents;
    }

    public void setDocuments(List<ProjectDocumentStatus> documents) {
        this.documents = documents;
    }

    public boolean isProjectManager() {
        return projectManager;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        AllDocumentsViewModel that = (AllDocumentsViewModel) o;

        return new EqualsBuilder()
                .append(projectId, that.projectId)
                .append(projectName, that.projectName)
                .append(documents, that.documents)
                .append(projectManager, that.projectManager)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(projectId)
                .append(projectName)
                .append(documents)
                .append(projectManager)
                .toHashCode();
    }
}


