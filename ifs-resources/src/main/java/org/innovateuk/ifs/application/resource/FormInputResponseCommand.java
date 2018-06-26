package org.innovateuk.ifs.application.resource;


import java.io.Serializable;

/**
 * A object to permission around.
 */
public class FormInputResponseCommand implements Serializable {

    private long formInputId;
    private long applicationId;
    private long userId;
    private String value;

    public FormInputResponseCommand(long formInputId, long applicationId, long userId, String value) {
        this.formInputId = formInputId;
        this.applicationId = applicationId;
        this.userId = userId;
        this.value = value;
    }

    public long getFormInputId() {
        return formInputId;
    }

    public void setFormInputId(long formInputId) {
        this.formInputId = formInputId;
    }

    public long getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(long applicationId) {
        this.applicationId = applicationId;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
