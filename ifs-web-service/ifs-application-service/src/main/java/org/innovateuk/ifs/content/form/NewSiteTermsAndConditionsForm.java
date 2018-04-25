package org.innovateuk.ifs.content.form;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.innovateuk.ifs.controller.BaseBindingResultTarget;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.NotNull;

public class NewSiteTermsAndConditionsForm extends BaseBindingResultTarget {

    @NotNull(message = "{validation.newtermsandconditionsform.agree.required}")
    @AssertTrue(message = "{validation.newtermsandconditionsform.agree.required}")
    private Boolean agree;

    public Boolean getAgree() {
        return agree;
    }

    public void setAgree(final Boolean agree) {
        this.agree = agree;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final NewSiteTermsAndConditionsForm that = (NewSiteTermsAndConditionsForm) o;

        return new EqualsBuilder()
                .append(agree, that.agree)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(agree)
                .toHashCode();
    }
}
