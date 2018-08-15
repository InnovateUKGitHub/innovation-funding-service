package org.innovateuk.ifs.competitionsetup.projectdocument.form;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.innovateuk.ifs.controller.BaseBindingResultTarget;

import java.util.Set;

/**
 * Form to capture the details of the new project document
 */
public class LandingPageForm extends BaseBindingResultTarget {

    private Set<Long> enabledIds;

    public Set<Long> getEnabledIds() {
        return enabledIds;
    }

    public void setEnabledIds(Set<Long> enabledIds) {
        this.enabledIds = enabledIds;
    }

    public LandingPageForm() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        LandingPageForm that = (LandingPageForm) o;

        return new EqualsBuilder()
                .append(enabledIds, that.enabledIds)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(enabledIds)
                .toHashCode();
    }
}


