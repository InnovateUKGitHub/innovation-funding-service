package org.innovateuk.ifs.application.forms.sections.yourorganisation.form;

/**
 * TODO DW - document this class
 */
public class GrowthTableRow {

    // TODO DW - to remove when this no longer resides within the FormInput model
    private long formInputId;

    private String description;
    private Long value;

    public GrowthTableRow(long formInputId, String description, Long value) {
        this.formInputId = formInputId;
        this.description = description;
        this.value = value;
    }

    public long getFormInputId() {
        return formInputId;
    }

    public String getDescription() {
        return description;
    }

    public Long getValue() {
        return value;
    }
}
