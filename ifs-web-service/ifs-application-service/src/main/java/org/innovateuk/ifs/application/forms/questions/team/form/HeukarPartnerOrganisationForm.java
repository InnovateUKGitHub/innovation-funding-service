package org.innovateuk.ifs.application.forms.questions.team.form;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.innovateuk.ifs.registration.form.OrganisationTypeForm;

import javax.validation.constraints.NotNull;

public class HeukarPartnerOrganisationForm {


    private Long id;

    @NotNull(message="{validation.standard.organisationtype.required}")
    private Long organisationTypeId;

    private boolean isLeadApplicant;

    public Long getOrganisationTypeId() {
        return organisationTypeId;
    }

    public void setOrganisationTypeId(Long organisationTypeId) {
        this.organisationTypeId = organisationTypeId;
    }

    public boolean isLeadApplicant() {
        return isLeadApplicant;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        OrganisationTypeForm that = (OrganisationTypeForm) o;

        return new EqualsBuilder()
                .append(isLeadApplicant, that.isLeadApplicant())
                .append(organisationTypeId, that.getOrganisationType())
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(organisationTypeId)
                .append(isLeadApplicant)
                .toHashCode();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
