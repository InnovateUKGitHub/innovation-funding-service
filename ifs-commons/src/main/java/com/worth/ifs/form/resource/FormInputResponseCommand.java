package com.worth.ifs.form.resource;


import java.io.Serializable;

/**
 * A object to permission around.
 * TODO It would be preferable to have a key instead. However the code that this class secures is both a create and
 * TODO an update, and with a key there will be no entity to return on a create. Thus we need to split out the service
 * TODO methods to be separate.
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
