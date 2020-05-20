package org.innovateuk.ifs.project.projectdetails.form;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.innovateuk.ifs.controller.BaseBindingResultTarget;

import javax.validation.constraints.NotNull;

/**
 * Form for capturing the partner project location
 */
public class PartnerProjectLocationForm extends BaseBindingResultTarget {

    private String postcode;
    private String town;

    // for spring form binding
    public PartnerProjectLocationForm() {
    }

    public PartnerProjectLocationForm(String postcode, String town) {
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

        PartnerProjectLocationForm that = (PartnerProjectLocationForm) o;

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

