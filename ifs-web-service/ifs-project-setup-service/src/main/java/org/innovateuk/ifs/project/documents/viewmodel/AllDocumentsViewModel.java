package org.innovateuk.ifs.project.documents.viewmodel;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.innovateuk.ifs.project.document.resource.DocumentStatus;

import java.util.Map;

public class AllDocumentsViewModel {

    private Long projectId;
    private String projectName;
    private Map<String, DocumentStatus> documents;

    public AllDocumentsViewModel(Long projectId, String projectName, Map<String, DocumentStatus> documents) {
        this.projectId = projectId;
        this.projectName = projectName;
        this.documents = documents;
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

    public Map<String, DocumentStatus> getDocuments() {
        return documents;
    }

    public void setDocuments(Map<String, DocumentStatus> documents) {
        this.documents = documents;
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
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(projectId)
                .append(projectName)
                .append(documents)
                .toHashCode();
    }
}


