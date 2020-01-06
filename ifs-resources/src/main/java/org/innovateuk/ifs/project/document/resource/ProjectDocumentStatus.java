package org.innovateuk.ifs.project.document.resource;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.EnumSet;

import static org.innovateuk.ifs.project.document.resource.DocumentStatus.*;

/*
 * This class is for getting the status of project document.
 */

public class ProjectDocumentStatus {

    private Long documentConfigId;

    private String title;

    private DocumentStatus status;

    public ProjectDocumentStatus(Long documentConfigId, String title, DocumentStatus status) {
        this.documentConfigId = documentConfigId;
        this.title = title;
        this.status = status;
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

    public DocumentStatus getStatus() {
        return status;
    }

    public boolean isNotSubmitted() {
        return EnumSet.of(UNSET, UPLOADED).contains(status);
    }

    public boolean isAwaitingReview() {
        return status == SUBMITTED;
    }

    public boolean isApproved() {
        return status == APPROVED;
    }

    public boolean isRejected() {
        return EnumSet.of(REJECTED, REJECTED_DUE_TO_TEAM_CHANGE).contains(status);
    }

    public void setStatus(DocumentStatus status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        ProjectDocumentStatus that = (ProjectDocumentStatus) o;

        return new EqualsBuilder()
                .append(documentConfigId, that.documentConfigId)
                .append(title, that.title)
                .append(status, that.status)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(documentConfigId)
                .append(title)
                .append(status)
                .toHashCode();
    }
}
