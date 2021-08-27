package org.innovateuk.ifs.documents.viewModel;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.innovateuk.ifs.file.controller.viewmodel.FileDetailsViewModel;
import org.innovateuk.ifs.project.document.resource.DocumentStatus;

import java.time.LocalDate;

/**
 * View model for viewing/actions on each document
 */
public class DocumentViewModel {

    private final long projectId;
    private final String projectName;
    private final long applicationId;
    private final long documentConfigId;
    private final String title;
    private final String guidance;
    private final FileDetailsViewModel fileDetails;
    private final DocumentStatus status;
    private final String statusComments;
    private final boolean projectManager;
    private final boolean projectIsActive;
    private final boolean isSuperAdminUser;
    private final boolean userCanApproveOrRejectDocuments;
    private final String statusModifiedBy;
    private final LocalDate statusModifiedDate;
    private final boolean isStatusModifiedByLoggedInUser;
    private final boolean statusModifiedByMO;
    private final boolean isInternalUser;

    public DocumentViewModel(long projectId,
                             String projectName,
                             long applicationId,
                             long documentConfigId,
                             String title,
                             String guidance,
                             FileDetailsViewModel fileDetails,
                             DocumentStatus status,
                             String statusComments,
                             boolean projectManager,
                             boolean projectIsActive,
                             boolean isSuperAdminUser,
                             boolean userCanApproveOrRejectDocuments,
                             String statusModifiedBy,
                             LocalDate statusModifiedDate,
                             boolean isStatusModifiedByLoggedInUser,
                             boolean statusModifiedByMO,
                             boolean isInternalUser) {
        this.projectId = projectId;
        this.projectName = projectName;
        this.applicationId = applicationId;
        this.documentConfigId = documentConfigId;
        this.title = title;
        this.guidance = guidance;
        this.fileDetails = fileDetails;
        this.status = status;
        this.statusComments = statusComments;
        this.projectManager = projectManager;
        this.projectIsActive = projectIsActive;
        this.isSuperAdminUser = isSuperAdminUser;
        this.userCanApproveOrRejectDocuments = userCanApproveOrRejectDocuments;
        this.statusModifiedBy = statusModifiedBy;
        this.statusModifiedDate = statusModifiedDate;
        this.isStatusModifiedByLoggedInUser = isStatusModifiedByLoggedInUser;
        this.statusModifiedByMO = statusModifiedByMO;
        this.isInternalUser = isInternalUser;
    }

    public long getProjectId() {
        return projectId;
    }

    public String getProjectName() {
        return projectName;
    }

    public long getApplicationId() {
        return applicationId;
    }

    public long getDocumentConfigId() {
        return documentConfigId;
    }

    public String getTitle() {
        return title;
    }

    public FileDetailsViewModel getFileDetails() {
        return fileDetails;
    }

    public DocumentStatus getStatus() {
        return status;
    }

    public String getStatusComments() {
        return statusComments;
    }

    public String getGuidance() {
        return guidance;
    }

    public boolean isProjectManager() {
        return projectManager;
    }

    public boolean isProjectIsActive() {
        return projectIsActive;
    }

    public boolean isSuperAdminUser() {
        return isSuperAdminUser;
    }

    public boolean isUserCanApproveOrRejectDocuments() {
        return userCanApproveOrRejectDocuments;
    }

    public String getStatusModifiedBy() {
        return statusModifiedBy;
    }

    public LocalDate getStatusModifiedDate() {
        return statusModifiedDate;
    }

    public boolean isStatusModifiedByLoggedInUser() {
        return isStatusModifiedByLoggedInUser;
    }

    public boolean isStatusModifiedByMO() {
        return statusModifiedByMO;
    }

    public boolean isInternalUser() {
        return isInternalUser;
    }

    public boolean isEditable() {
        return projectManager && status != DocumentStatus.APPROVED && status != DocumentStatus.SUBMITTED;
    }

    public boolean isShowDisabledSubmitDocumentsButton() {
        return projectManager && (status == DocumentStatus.UNSET  || status == DocumentStatus.REJECTED);
    }

    public boolean isShowRejectDocumentButtonWhenDocumentIsApproved() {
        return isSuperAdminUser && projectIsActive;
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
                .append(guidance, that.guidance)
                .append(projectManager, that.projectManager)
                .append(projectIsActive, that.projectIsActive)
                .append(userCanApproveOrRejectDocuments, that.userCanApproveOrRejectDocuments)
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
                .append(guidance)
                .append(projectManager)
                .append(projectIsActive)
                .append(userCanApproveOrRejectDocuments)
                .toHashCode();
    }
}