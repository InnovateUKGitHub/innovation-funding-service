package org.innovateuk.ifs.project.documents.viewmodel;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.innovateuk.ifs.project.document.resource.ProjectDocumentStatus;

import java.util.List;

/**
 * View model for viewing all documents
 */
public class AllDocumentsViewModel {

    private long competitionId;
    private long applicationId;
    private long projectId;
    private String projectName;
    private List<ProjectDocumentStatus> documents;

    public AllDocumentsViewModel(long competitionId, long applicationId, long projectId, String projectName, List<ProjectDocumentStatus> documents) {
        this.competitionId = competitionId;
        this.applicationId = applicationId;
        this.projectId = projectId;
        this.projectName = projectName;
        this.documents = documents;
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

    public List<ProjectDocumentStatus> getDocuments() {
        return documents;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        AllDocumentsViewModel that = (AllDocumentsViewModel) o;

        return new EqualsBuilder()
                .append(competitionId, that.competitionId)
                .append(applicationId, that.applicationId)
                .append(projectId, that.projectId)
                .append(projectName, that.projectName)
                .append(documents, that.documents)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(competitionId)
                .append(applicationId)
                .append(projectId)
                .append(projectName)
                .append(documents)
                .toHashCode();
    }
}