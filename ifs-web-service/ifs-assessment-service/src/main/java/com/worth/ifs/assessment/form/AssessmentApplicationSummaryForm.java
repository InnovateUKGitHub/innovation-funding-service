package com.worth.ifs.assessment.form;

import com.worth.ifs.controller.BindingResultTarget;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;

import java.util.List;

/**
 * Form field model for the decision made by an assessor about an application while reviewing the assessment summary.
 */
public class AssessmentApplicationSummaryForm implements BindingResultTarget {

    private Boolean fundingConfirmation;
    private String feedback;
    private String comments;
    private BindingResult bindingResult;
    private List<ObjectError> objectErrors;

    public AssessmentApplicationSummaryForm() {
    }

    public AssessmentApplicationSummaryForm(Boolean fundingConfirmation, String feedback, String comments, BindingResult bindingResult, List<ObjectError> objectErrors) {
        this.fundingConfirmation = fundingConfirmation;
        this.feedback = feedback;
        this.comments = comments;
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

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
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

        AssessmentApplicationSummaryForm that = (AssessmentApplicationSummaryForm) o;

        return new EqualsBuilder()
                .append(fundingConfirmation, that.fundingConfirmation)
                .append(feedback, that.feedback)
                .append(comments, that.comments)
                .append(bindingResult, that.bindingResult)
                .append(objectErrors, that.objectErrors)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(fundingConfirmation)
                .append(feedback)
                .append(comments)
                .append(bindingResult)
                .append(objectErrors)
                .toHashCode();
    }
}
