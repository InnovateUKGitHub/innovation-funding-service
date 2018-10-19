package org.innovateuk.ifs.project.documents.form;

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
}
