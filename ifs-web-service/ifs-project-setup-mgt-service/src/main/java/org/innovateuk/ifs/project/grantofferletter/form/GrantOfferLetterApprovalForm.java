package org.innovateuk.ifs.project.grantofferletter.form;

import org.innovateuk.ifs.project.resource.ApprovalType;

/**
 * Form to capture the approval or rejection response, along with the rejection reason, if applicable.
 */
public class GrantOfferLetterApprovalForm {

    private ApprovalType approvalType;

    private String rejectionReason;

    public GrantOfferLetterApprovalForm() {
    }

    public GrantOfferLetterApprovalForm(ApprovalType approvalType, String rejectionReason) {
        this.approvalType = approvalType;
        this.rejectionReason = rejectionReason;
    }

    public ApprovalType getApprovalType() {
        return approvalType;
    }

    public void setApprovalType(ApprovalType approvalType) {
        this.approvalType = approvalType;
    }

    public String getRejectionReason() {
        return rejectionReason;
    }

    public void setRejectionReason(String rejectionReason) {
        this.rejectionReason = rejectionReason;
    }
}
