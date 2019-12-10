package org.innovateuk.ifs.application.forms.sections.yourorganisation.form;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * Represents a radio option.
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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        FormOption that = (FormOption) o;

        return new EqualsBuilder()
            .append(description, that.description)
            .append(value, that.value)
            .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
            .append(description)
            .append(value)
            .toHashCode();
    }
}
