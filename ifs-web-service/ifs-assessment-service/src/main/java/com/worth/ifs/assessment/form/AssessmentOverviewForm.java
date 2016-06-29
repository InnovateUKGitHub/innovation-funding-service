package com.worth.ifs.assessment.form;


import com.worth.ifs.application.form.Form;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;

import java.util.List;

/**
 * Form field model for the assessment rejection content
 */
public class AssessmentOverviewForm extends Form {

    private String rejectReason;
    private String rejectInformation;
    private BindingResult bindingResult;
    private List<ObjectError> objectErrors;

    public AssessmentOverviewForm() {
    }

    public AssessmentOverviewForm(String rejectReason, String rejectInformation, BindingResult bindingResult, List<ObjectError> objectErrors) {
        this.rejectReason = rejectReason;
        this.rejectInformation = rejectInformation;
        this.bindingResult = bindingResult;
        this.objectErrors = objectErrors;
    }

    public String getRejectReason() {
        return rejectReason;
    }

    public void setRejectReason(String rejectReason) {
        this.rejectReason = rejectReason;
    }

    public String getRejectInformation() {
        return rejectInformation;
    }

    public void setRejectInformation(String rejectInformation) {
        this.rejectInformation = rejectInformation;
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

        AssessmentOverviewForm that = (AssessmentOverviewForm) o;

        return new EqualsBuilder()
                .append(rejectReason, that.rejectReason)
                .append(rejectInformation, that.getRejectInformation())
                .append(bindingResult, that.bindingResult)
                .append(objectErrors, that.objectErrors)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(rejectReason)
                .append(rejectInformation)
                .append(bindingResult)
                .append(objectErrors)
                .toHashCode();
    }
}
