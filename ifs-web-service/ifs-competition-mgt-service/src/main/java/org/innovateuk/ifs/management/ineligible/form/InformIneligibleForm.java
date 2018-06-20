package org.innovateuk.ifs.management.ineligible.form;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.validator.constraints.NotBlank;
import org.innovateuk.ifs.controller.BindingResultTarget;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;

import java.util.List;

/**
 * Form for informing an applicant that their application is ineligible
 */
public class InformIneligibleForm implements BindingResultTarget {

    @NotBlank(message = "{validation.informleadapplicant.subject.required}")
    private String subject;

    @NotBlank(message = "{validation.informleadapplicant.message.required}")
    private String message;

    private BindingResult bindingResult;
    private List<ObjectError> objectErrors;

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
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
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        InformIneligibleForm that = (InformIneligibleForm) o;

        return new EqualsBuilder()
                .append(subject, that.subject)
                .append(message, that.message)
                .append(bindingResult, that.bindingResult)
                .append(objectErrors, that.objectErrors)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(subject)
                .append(message)
                .append(bindingResult)
                .append(objectErrors)
                .toHashCode();
    }
}
