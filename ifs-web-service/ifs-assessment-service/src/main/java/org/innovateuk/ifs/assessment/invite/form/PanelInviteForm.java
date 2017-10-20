package org.innovateuk.ifs.assessment.invite.form;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.innovateuk.ifs.commons.validation.constraints.WordCount;
import org.innovateuk.ifs.controller.BaseBindingResultTarget;
import org.innovateuk.ifs.invite.resource.RejectionReasonResource;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * Form field model for the competition rejection content
 */
public class PanelInviteForm extends BaseBindingResultTarget {

    @NotNull(message = "{validation.competitioninvitedecision.required}")
    private Boolean acceptInvitation;

    private RejectionReasonResource rejectReason;
    @Size(max = 5000, message = "{validation.field.too.many.characters}")
    @WordCount(max = 100, message = "{validation.field.max.word.count}")
    private String rejectComment;

    public Boolean getAcceptInvitation() {
        return acceptInvitation;
    }

    public void setAcceptInvitation(Boolean acceptInvitation) {
        this.acceptInvitation = acceptInvitation;
    }

    @AssertTrue(message = "{validation.rejectcompetitionform.rejectReason.required}")
    public boolean isRejectReasonValid() {
        return BooleanUtils.isNotFalse(acceptInvitation) || rejectReason != null;
    }

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
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        PanelInviteForm that = (PanelInviteForm) o;

        return new EqualsBuilder()
                .append(acceptInvitation, that.acceptInvitation)
                .append(rejectReason, that.rejectReason)
                .append(rejectComment, that.rejectComment)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(acceptInvitation)
                .append(rejectReason)
                .append(rejectComment)
                .toHashCode();
    }
}
