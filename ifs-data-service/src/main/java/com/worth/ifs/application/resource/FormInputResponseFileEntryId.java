package com.worth.ifs.application.resource;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;

/**
 * Represents a compound key for looking up a FormInputResponse, or potentially discovering that one does not yet
 * exist for this combination of ids
 */
public class FormInputResponseFileEntryId implements Serializable {

    private long formInputId;
    private long applicationId;
    private long processRoleId;

    public FormInputResponseFileEntryId() {
        // for JSON marshalling
    }

    public FormInputResponseFileEntryId(long formInputId, long applicationId, long processRoleId) {
        this.formInputId = formInputId;
        this.applicationId = applicationId;
        this.processRoleId = processRoleId;
    }

    public long getFormInputId() {
        return formInputId;
    }

    public long getApplicationId() {
        return applicationId;
    }

    public long getProcessRoleId() {
        return processRoleId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        FormInputResponseFileEntryId that = (FormInputResponseFileEntryId) o;

        return new EqualsBuilder()
                .append(formInputId, that.formInputId)
                .append(applicationId, that.applicationId)
                .append(processRoleId, that.processRoleId)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(formInputId)
                .append(applicationId)
                .append(processRoleId)
                .toHashCode();
    }
}
