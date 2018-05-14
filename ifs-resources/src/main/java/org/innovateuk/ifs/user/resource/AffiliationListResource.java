package org.innovateuk.ifs.user.resource;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import javax.validation.Valid;
import java.util.List;

/**
 * An affiliation of a User. It may be used to describe personal and family appointments held, personal affiliations, other financial interests, and family financial interests depending on the {@link AffiliationType}.
 */
public class AffiliationListResource {

    @Valid
    private List<AffiliationResource> affiliationResourceList;

    public AffiliationListResource() {
    }

    public AffiliationListResource(List<AffiliationResource> affiliationResourceList) {
        this.affiliationResourceList = affiliationResourceList;
    }

    public List<AffiliationResource> getAffiliationResourceList() {
        return affiliationResourceList;
    }

    public void setAffiliationResourceList(List<AffiliationResource> affiliationResourceList) {
        this.affiliationResourceList = affiliationResourceList;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        AffiliationListResource that = (AffiliationListResource) o;

        return new EqualsBuilder()
                .append(affiliationResourceList, that.affiliationResourceList)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(affiliationResourceList)
                .toHashCode();
    }
}
