package org.innovateuk.ifs.management.viewmodel;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * Holder of model attributes for the available applications shown in the 'Find' tab of the Assessment Interview Panel invite applications view.
 */
public class InterviewPanelApplicationRowViewModel {

    private final long id;
    private final String name;
    private final String leadOrganisation;

    public InterviewPanelApplicationRowViewModel(long id, String name, String leadOrganisation) {
        this.id = id;
        this.name = name;
        this.leadOrganisation = leadOrganisation;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getLeadOrganisation() {
        return leadOrganisation;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        InterviewPanelApplicationRowViewModel that = (InterviewPanelApplicationRowViewModel) o;

        return new EqualsBuilder()
                .append(id, that.id)
                .append(name, that.name)
                .append(leadOrganisation, that.leadOrganisation)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(id)
                .append(name)
                .append(leadOrganisation)
                .toHashCode();
    }
}