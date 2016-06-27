package com.worth.ifs.assessment.form;

import com.worth.ifs.controller.BindingResultTarget;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;

import java.util.List;

/**
 * Form field model for the assessment feedback given for a question.
 */
public class AssessmentFeedbackForm implements BindingResultTarget {

    private String value;
    private Integer score;
    private BindingResult bindingResult;
    private List<ObjectError> objectErrors;

    public AssessmentFeedbackForm() {
    }

    public AssessmentFeedbackForm(String value, Integer score, BindingResult bindingResult, List<ObjectError> objectErrors) {
        this.value = value;
        this.score = score;
        this.bindingResult = bindingResult;
        this.objectErrors = objectErrors;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public BindingResult getBindingResult() {
        return bindingResult;
    }

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

        AssessmentFeedbackForm that = (AssessmentFeedbackForm) o;

        return new EqualsBuilder()
                .append(value, that.value)
                .append(score, that.score)
                .append(bindingResult, that.bindingResult)
                .append(objectErrors, that.objectErrors)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(value)
                .append(score)
                .append(bindingResult)
                .append(objectErrors)
                .toHashCode();
    }
}
