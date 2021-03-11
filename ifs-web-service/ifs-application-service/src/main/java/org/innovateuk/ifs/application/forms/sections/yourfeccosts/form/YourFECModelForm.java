package org.innovateuk.ifs.application.forms.sections.yourfeccosts.form;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import javax.validation.constraints.NotNull;

/**
 * Form used to capture project fEC Model information
 */
public class YourFECModelForm {

    @NotNull(message = "{validation.finance.fecmodel.fecModelEnabled.blank}")
    private Boolean fecModelEnabled;

    public Boolean getFecModelEnabled() {
        return fecModelEnabled;
    }

    public void setFecModelEnabled(Boolean fecModelEnabled) {
        this.fecModelEnabled = fecModelEnabled;
    }

    public YourFECModelForm() {
    }

    public YourFECModelForm(Boolean fecModelEnabled) {
        this.fecModelEnabled = fecModelEnabled;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        YourFECModelForm that = (YourFECModelForm) o;

        return new EqualsBuilder()
                .append(fecModelEnabled, that.fecModelEnabled)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(fecModelEnabled)
                .toHashCode();
    }
}