package org.innovateuk.ifs.documents.viewModel;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.innovateuk.ifs.project.document.resource.ProjectDocumentStatus;
import org.innovateuk.ifs.project.resource.ProjectResource;

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
    private boolean projectManager;
    private boolean collaborativeProject;
    private boolean isProcurement;
    private boolean userCanApproveOrRejectDocuments;

    public AllDocumentsViewModel(ProjectResource project, List<ProjectDocumentStatus> documents, boolean projectManager, boolean IsProcurement, boolean userCanApproveOrRejectDocuments) {
        this.competitionId = project.getCompetition();
        this.applicationId = project.getApplication();
        this.projectId = project.getId();
        this.projectName = project.getName();
        this.documents = documents;
        this.projectManager = projectManager;
        this.collaborativeProject = project.isCollaborativeProject();
        this.isProcurement = IsProcurement;
        this.userCanApproveOrRejectDocuments = userCanApproveOrRejectDocuments;
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

    public boolean isProjectManager() {
        return projectManager;
    }

    public boolean isCollaborativeProject() {
        return collaborativeProject;
    }

    public boolean IsProcurement() {
        return isProcurement;
    }

    public boolean isUserCanApproveOrRejectDocuments() {
        return userCanApproveOrRejectDocuments;
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
                .append(projectManager, that.projectManager)
                .append(isProcurement, that.isProcurement)
                .append(userCanApproveOrRejectDocuments, that.userCanApproveOrRejectDocuments)
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
                .append(projectManager)
                .append(isProcurement)
                .append(userCanApproveOrRejectDocuments)
                .toHashCode();
    }
}