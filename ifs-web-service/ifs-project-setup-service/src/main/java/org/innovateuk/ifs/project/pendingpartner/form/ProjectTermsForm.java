package org.innovateuk.ifs.project.pendingpartner.form;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.innovateuk.ifs.controller.BaseBindingResultTarget;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.NotNull;

public class ProjectTermsForm extends BaseBindingResultTarget {

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

        ProjectTermsForm that = (ProjectTermsForm) o;

        return new EqualsBuilder()
                .append(agreed, that.agreed)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(agreed)
                .toHashCode();
    }

    @Override
    public String toString() {
        return "ProjectTermsForm{" +
                "agreed=" + agreed +
                '}';
    }
}
