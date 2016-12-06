package com.worth.ifs.competition.resource;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.Size;

public class GuidanceRowResource {

    private Long id;

    @NotEmpty(message = "{validation.applicationquestionform.subject.required}")
    @Size(max=255, message = "{validation.applicationquestionform.subject.max}")
    private String subject;

    @NotEmpty(message = "{validation.applicationquestionform.justification.required}")
    @Size(max=255, message = "{validation.applicationquestionform.justification.max}")
    private String justification;

    private Long formInput;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getJustification() {
        return justification;
    }

    public void setJustification(String justification) {
        this.justification = justification;
    }

    public Long getFormInput() {
        return formInput;
    }

    public void setFormInput(Long formInput) {
        this.formInput = formInput;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        GuidanceRowResource that = (GuidanceRowResource) o;

        return new EqualsBuilder()
                .append(id, that.id)
                .append(subject, that.subject)
                .append(justification, that.justification)
                .append(formInput, that.formInput)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(id)
                .append(subject)
                .append(justification)
                .append(formInput)
                .toHashCode();
    }
}
