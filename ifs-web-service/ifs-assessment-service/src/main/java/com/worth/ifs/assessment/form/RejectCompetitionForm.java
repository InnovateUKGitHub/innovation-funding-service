package com.worth.ifs.assessment.form;

import com.worth.ifs.controller.BaseBindingResultTarget;
import com.worth.ifs.invite.resource.RejectionReasonResource;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * Form field model for the competition rejection content
 */
public class RejectCompetitionForm extends BaseBindingResultTarget {

    @NotNull(message = "{validation.rejectcompetitionform.rejectReason.required}")
    private RejectionReasonResource rejectReason;
    @Size(max = 5000, message = "{validation.field.too.many.characters}")
    private String rejectComment;

    public RejectionReasonResource getRejectReason() {
        return rejectReason;
    }

    public void setRejectReason(RejectionReasonResource rejectReason) {
        this.rejectReason = rejectReason;
    }

    public String getRejectComment() {
        return rejectComment;
    }

    public void setRejectComment(String rejectComment) {
        this.rejectComment = rejectComment;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        RejectCompetitionForm that = (RejectCompetitionForm) o;

        return new EqualsBuilder()
                .append(rejectReason, that.rejectReason)
                .append(rejectComment, that.rejectComment)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(rejectReason)
                .append(rejectComment)
                .toHashCode();
    }
}