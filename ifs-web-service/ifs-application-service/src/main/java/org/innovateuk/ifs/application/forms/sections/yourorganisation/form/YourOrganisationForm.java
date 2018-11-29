package org.innovateuk.ifs.application.forms.sections.yourorganisation.form;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * Form used to capture project location information
 */
public class YourOrganisationForm {

    private String postcode;

    public YourOrganisationForm(String postcode) {
        this.postcode = postcode;
    }

    YourOrganisationForm() {
    }

    public String getPostcode() {
        return postcode;
    }

    public void setPostcode(String postcode) {
        this.postcode = postcode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        YourOrganisationForm that = (YourOrganisationForm) o;

        return new EqualsBuilder()
                .append(postcode, that.postcode)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(postcode)
                .toHashCode();
    }
}
