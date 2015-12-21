package com.worth.ifs.application.resource;

/**
 * Represents a compound key for looking up a FormInputResponse, or potentially discovering that one does not yet
 * exist for this combination of ids
 */
public class FormInputResponseFileEntryId {

    private long formInputId;
    private long applicationId;
    private long processRoleId;

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
}
