package com.worth.ifs.invite.resource;

/**
 * TODO Class comment
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
}
