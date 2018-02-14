package org.innovateuk.ifs.project.grantofferletter.resource;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.innovateuk.ifs.project.resource.ApprovalType;

public class GrantOfferLetterApprovalResource {

    private ApprovalType approvalType;

    private String rejectionReason;

    public GrantOfferLetterApprovalResource() {
    }

    public GrantOfferLetterApprovalResource(ApprovalType approvalType, String rejectionReason) {
        this.approvalType = approvalType;
        this.rejectionReason = rejectionReason;
    }

    public ApprovalType getApprovalType() {
        return approvalType;
    }

    public String getRejectionReason() {
        return rejectionReason;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        GrantOfferLetterApprovalResource that = (GrantOfferLetterApprovalResource) o;

        return new EqualsBuilder()
                .append(approvalType, that.approvalType)
                .append(rejectionReason, that.rejectionReason)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(approvalType)
                .append(rejectionReason)
                .toHashCode();
    }
}
