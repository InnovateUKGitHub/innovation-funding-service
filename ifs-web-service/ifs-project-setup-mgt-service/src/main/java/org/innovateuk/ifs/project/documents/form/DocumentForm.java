package org.innovateuk.ifs.project.documents.form;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.innovateuk.ifs.controller.BaseBindingResultTarget;

/**
 * Form to capture whether the document has been accepted or rejected.
 */
public class DocumentForm extends BaseBindingResultTarget {

    private Boolean approved;

    private String rejectionReason;

    public DocumentForm() {
    }

    public Boolean getApproved() {
        return approved;
    }

    public void setApproved(Boolean approved) {
        this.approved = approved;
    }

    public String getRejectionReason() {
        return rejectionReason;
    }

    public void setRejectionReason(String rejectionReason) {
        this.rejectionReason = rejectionReason;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        DocumentForm that = (DocumentForm) o;

        return new EqualsBuilder()
                .append(approved, that.approved)
                .append(rejectionReason, that.rejectionReason)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(approved)
                .append(rejectionReason)
                .toHashCode();
    }
}
