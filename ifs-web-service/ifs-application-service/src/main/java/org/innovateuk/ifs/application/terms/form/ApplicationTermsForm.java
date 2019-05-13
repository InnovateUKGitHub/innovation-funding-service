package org.innovateuk.ifs.application.terms.form;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.innovateuk.ifs.controller.BaseBindingResultTarget;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.NotNull;

public class ApplicationTermsForm extends BaseBindingResultTarget {

    @NotNull(message = "{validation.application.terms.accept.required}")
    @AssertTrue(message = "{validation.application.terms.accept.required}")
    private Boolean agreed;

    public Boolean getAgreed() {
        return agreed;
    }

    public void setAgreed(Boolean agreed) {
        this.agreed = agreed;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        ApplicationTermsForm that = (ApplicationTermsForm) o;

        return new EqualsBuilder()
                .append(getAgreed(), that.getAgreed())
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(getAgreed())
                .toHashCode();
    }
}