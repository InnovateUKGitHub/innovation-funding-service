package org.innovateuk.ifs.application.forms.sections.yourprojectlocation.form;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * Form used to capture project location information
 */
public class YourProjectLocationForm {

    private String postcode;
    private String town;

    public YourProjectLocationForm(String postcode, String town) {
        this.postcode = postcode;
        this.town = town;
    }

    public String getPostcode() {
        return postcode;
    }

    public void setPostcode(String postcode) {
        this.postcode = postcode;
    }

    public String getTown() {
        return town;
    }

    public void setTown(String town) {
        this.town = town;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        YourProjectLocationForm that = (YourProjectLocationForm) o;

        return new EqualsBuilder()
                .append(postcode, that.postcode)
                .append(town, that.town)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(postcode)
                .append(town)
                .toHashCode();
    }
}
