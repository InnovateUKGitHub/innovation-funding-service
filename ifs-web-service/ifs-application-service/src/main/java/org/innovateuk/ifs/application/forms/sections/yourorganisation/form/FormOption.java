package org.innovateuk.ifs.application.forms.sections.yourorganisation.form;

/**
 * TODO DW - document this class
 */
public class FormOption {

    private String description;
    private String value;

    public FormOption(String description, String value) {
        this.description = description;
        this.value = value;
    }

    public String getDescription() {
        return description;
    }

    public String getValue() {
        return value;
    }
}
