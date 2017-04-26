package org.innovateuk.ifs.management.viewmodel;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * View model for Competition Management Ineligible Applications table rows.
 */
public class IneligibleApplicationsRowViewModel extends BaseApplicationsRowViewModel {

    private String leadApplicant;
    private boolean informed;

    public IneligibleApplicationsRowViewModel(long id,
                                              String projectTitle,
                                              String lead,
                                              String leadApplicant,
                                              boolean informed) {
        super(id, projectTitle, lead);
        this.leadApplicant = leadApplicant;
        this.informed = informed;
    }

    public String getLeadApplicant() {
        return leadApplicant;
    }

    public boolean isInformed() {
        return informed;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        IneligibleApplicationsRowViewModel that = (IneligibleApplicationsRowViewModel) o;

        return new EqualsBuilder()
                .appendSuper(super.equals(o))
                .append(informed, that.informed)
                .append(leadApplicant, that.leadApplicant)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .appendSuper(super.hashCode())
                .append(leadApplicant)
                .append(informed)
                .toHashCode();
    }
}
