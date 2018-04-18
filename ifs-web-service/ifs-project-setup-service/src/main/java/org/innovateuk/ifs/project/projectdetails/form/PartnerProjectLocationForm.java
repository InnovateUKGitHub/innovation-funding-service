package org.innovateuk.ifs.project.projectdetails.form;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.innovateuk.ifs.controller.BaseBindingResultTarget;

import javax.validation.constraints.NotNull;

/**
 * Form for capturing the partner project location
 */
public class PartnerProjectLocationForm extends BaseBindingResultTarget {

    @NotNull(message = "{validation.partnerprojectlocationform.postcode.required}")
    private String postCode;

    // for spring form binding
    public PartnerProjectLocationForm() {
    }

    public PartnerProjectLocationForm(String postCode) {
        this.postCode = postCode;
    }

    public String getPostCode() {
        return postCode;
    }

    public void setPostCode(String postCode) {
        this.postCode = postCode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        PartnerProjectLocationForm that = (PartnerProjectLocationForm) o;

        return new EqualsBuilder()
                .append(postCode, that.postCode)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(postCode)
                .toHashCode();
    }
}

