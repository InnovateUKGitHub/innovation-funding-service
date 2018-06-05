package org.innovateuk.ifs.assessment.resource;

import org.innovateuk.ifs.commons.validation.constraints.WordCount;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * DTO for rejecting invites to assess Applications.
 */
public class AssessmentRejectOutcomeResource {

    @NotNull(message = "{validation.assessmentRejectOutcome.rejectReason.required}")
    private AssessmentRejectOutcomeValue rejectReason;
    @Size(max = 5000, message = "{validation.field.too.many.characters}")
    @WordCount(max = 100, message = "{validation.field.max.word.count}")
    private String rejectComment;

    public AssessmentRejectOutcomeResource() {
    }

    public AssessmentRejectOutcomeResource(AssessmentRejectOutcomeValue rejectReason, String rejectComment) {
        this.rejectReason = rejectReason;
        this.rejectComment = rejectComment;
    }

    public AssessmentRejectOutcomeValue getRejectReason() {
        return rejectReason;
    }

    public void setRejectReason(AssessmentRejectOutcomeValue rejectReason) {
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

        AssessmentRejectOutcomeResource that = (AssessmentRejectOutcomeResource) o;

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
