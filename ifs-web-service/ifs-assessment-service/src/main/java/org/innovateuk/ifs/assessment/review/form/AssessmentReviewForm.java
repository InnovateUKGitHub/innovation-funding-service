package org.innovateuk.ifs.assessment.review.form;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.innovateuk.ifs.commons.validation.constraints.WordCount;
import org.innovateuk.ifs.controller.BaseBindingResultTarget;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * Form field model for the assessment review rejection content
 */
public class AssessmentReviewForm extends BaseBindingResultTarget {
    @NotNull
    private Boolean reviewAccept;
    @Size(max = 5000, message = "{validation.field.too.many.characters}")
    @WordCount(max = 100, message = "{validation.field.max.word.count}")
    private String rejectComment;


    public String getRejectComment() {
        return rejectComment;
    }

    public void setRejectComment(String rejectComment) {
        this.rejectComment = rejectComment;
    }

    public Boolean getReviewAccept() {
        return reviewAccept;
    }

    public void setReviewAccept(Boolean reviewAccept) {
        this.reviewAccept = reviewAccept;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        AssessmentReviewForm that = (AssessmentReviewForm) o;

        return new EqualsBuilder()
                .append(reviewAccept, that.reviewAccept)
                .append(rejectComment, that.rejectComment)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(reviewAccept)
                .append(rejectComment)
                .toHashCode();
    }
}