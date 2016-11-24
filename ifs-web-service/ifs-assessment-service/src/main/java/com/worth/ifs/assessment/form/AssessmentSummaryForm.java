package com.worth.ifs.assessment.form;

import com.worth.ifs.commons.validation.constraints.FieldRequiredIf;
import com.worth.ifs.commons.validation.constraints.WordCount;
import com.worth.ifs.controller.BindingResultTarget;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

/**
 * Form field model for the decision made by an assessor about an application while reviewing the assessment summary.
 */
@FieldRequiredIf(required = "feedback", argument = "fundingConfirmation", predicate = false, message = "{validation.assessmentFundingDecision.feedback.required}")
public class AssessmentSummaryForm implements BindingResultTarget {

    @NotNull(message = "{validation.assessmentFundingDecision.fundingConfirmation.required}")
    private Boolean fundingConfirmation;
    @Size(max = 5000, message = "{validation.field.too.many.characters}")
    @WordCount(max = 100, message = "{validation.field.max.word.count}")
    private String feedback;
    @Size(max = 5000, message = "{validation.field.too.many.characters}")
    @WordCount(max = 100, message = "{validation.field.max.word.count}")
    private String comment;
    private BindingResult bindingResult;
    private List<ObjectError> objectErrors;

    public AssessmentSummaryForm() {
    }

    public AssessmentSummaryForm(Boolean fundingConfirmation, String feedback, String comment, BindingResult bindingResult, List<ObjectError> objectErrors) {
        this.fundingConfirmation = fundingConfirmation;
        this.feedback = feedback;
        this.comment = comment;
        this.bindingResult = bindingResult;
        this.objectErrors = objectErrors;
    }

    public Boolean getFundingConfirmation() {
        return fundingConfirmation;
    }

    public void setFundingConfirmation(Boolean fundingConfirmation) {
        this.fundingConfirmation = fundingConfirmation;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    @Override
    public BindingResult getBindingResult() {
        return bindingResult;
    }

    @Override
    public void setBindingResult(BindingResult bindingResult) {
        this.bindingResult = bindingResult;
    }

    @Override
    public List<ObjectError> getObjectErrors() {
        return objectErrors;
    }

    @Override
    public void setObjectErrors(List<ObjectError> objectErrors) {
        this.objectErrors = objectErrors;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        AssessmentSummaryForm that = (AssessmentSummaryForm) o;

        return new EqualsBuilder()
                .append(fundingConfirmation, that.fundingConfirmation)
                .append(feedback, that.feedback)
                .append(comment, that.comment)
                .append(bindingResult, that.bindingResult)
                .append(objectErrors, that.objectErrors)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(fundingConfirmation)
                .append(feedback)
                .append(comment)
                .append(bindingResult)
                .append(objectErrors)
                .toHashCode();
    }
}
