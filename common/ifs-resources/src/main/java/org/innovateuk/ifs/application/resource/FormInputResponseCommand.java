package org.innovateuk.ifs.application.resource;


import java.io.Serializable;

/**
 * A object to permission around.
 * TODO: We are currently using this object for both create and update actions. IFS-3830 exists to tidy this up.
 */
public class FormInputResponseCommand implements Serializable {

    private long formInputId;
    private long applicationId;
    private long userId;
    private String value;
    private Long multipleChoiceOptionId;

    public FormInputResponseCommand(long formInputId, long applicationId, long userId, String value, Long multipleChoiceOptionId) {
        this.formInputId = formInputId;
        this.applicationId = applicationId;
        this.userId = userId;
        this.value = value;
        this.multipleChoiceOptionId = multipleChoiceOptionId;
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

    public Long getMultipleChoiceOptionId() {
        return multipleChoiceOptionId;
    }

    public void setMultipleChoiceOptionId(Long multipleChoiceOptionId) {
        this.multipleChoiceOptionId = multipleChoiceOptionId;
    }
}
