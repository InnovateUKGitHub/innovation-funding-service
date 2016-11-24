package com.worth.ifs.finance.resource;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * Application finance resource holds the organisation's finance resources for an application
 */
public class ProjectFinanceResource extends BaseFinanceResource {

    public Long getProject() {
        return super.getTarget();
    }

    public void setProject(Long target) {
        super.setTarget(target);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        ProjectFinanceResource that = (ProjectFinanceResource) o;

        return new EqualsBuilder()
                .append(id, that.id)
                .append(organisation, that.organisation)
                .append(target, that.target)
                .append(organisationSize, that.organisationSize)
                .append(financeOrganisationDetails, that.financeOrganisationDetails)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(id)
                .append(organisation)
                .append(target)
                .append(organisationSize)
                .append(financeOrganisationDetails)
                .toHashCode();
    }
}