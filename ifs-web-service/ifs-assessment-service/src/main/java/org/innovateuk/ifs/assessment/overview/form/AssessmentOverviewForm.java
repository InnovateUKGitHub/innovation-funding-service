package org.innovateuk.ifs.assessment.overview.form;


import com.fasterxml.jackson.annotation.JsonIgnore;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.innovateuk.ifs.assessment.resource.AssessmentRejectOutcomeValue;
import org.innovateuk.ifs.commons.validation.constraints.WordCount;
import org.innovateuk.ifs.controller.BaseBindingResultTarget;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * Form field model for the assessment rejection content
 */
public class AssessmentOverviewForm extends BaseBindingResultTarget {

    @NotNull(message = "{validation.assessmentRejectOutcome.rejectReason.required}")
    private AssessmentRejectOutcomeValue rejectReason;
    @Size(max = 5000, message = "{validation.field.too.many.characters}")
    @WordCount(max = 100, message = "{validation.field.max.word.count}")
    private String rejectComment;

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

    @JsonIgnore
    public AssessmentRejectOutcomeValue[] getAssessmentRejectOutcomeValues() {
        return AssessmentRejectOutcomeValue.values();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        AssessmentOverviewForm that = (AssessmentOverviewForm) o;

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
