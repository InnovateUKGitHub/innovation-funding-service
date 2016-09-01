package com.worth.ifs.invite.resource;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * DTO for rejecting invites to Competitions.
 */
public class CompetitionRejectionResource {

    private RejectionReasonResource rejectionReasonResource;

    private String rejectionComment;

    public CompetitionRejectionResource() {
    }

    public CompetitionRejectionResource(RejectionReasonResource rejectionReasonResource, String rejectionComment) {
        this.rejectionReasonResource = rejectionReasonResource;
        this.rejectionComment = rejectionComment;
    }

    public RejectionReasonResource getRejectionReasonResource() {
        return rejectionReasonResource;
    }

    public void setRejectionReasonResource(RejectionReasonResource rejectionReasonResource) {
        this.rejectionReasonResource = rejectionReasonResource;
    }

    public String getRejectionComment() {
        return rejectionComment;
    }

    public void setRejectionComment(String rejectionComment) {
        this.rejectionComment = rejectionComment;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        CompetitionRejectionResource that = (CompetitionRejectionResource) o;

        return new EqualsBuilder()
                .append(rejectionReasonResource, that.rejectionReasonResource)
                .append(rejectionComment, that.rejectionComment)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(rejectionReasonResource)
                .append(rejectionComment)
                .toHashCode();
    }
}
