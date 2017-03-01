package org.innovateuk.ifs.assessment.resource;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * Aggregate assessor scores for an Application.
 */
public class ApplicationAssessmentAggregateResource {

    private int totalScope;
    private int inScope;

    public ApplicationAssessmentAggregateResource() {
    }

    public ApplicationAssessmentAggregateResource(int totalScope, int inScope) {
        this.totalScope = totalScope;
        this.inScope = inScope;
    }

    public int getTotalScope() {
        return totalScope;
    }

    public int getInScope() {
        return inScope;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        ApplicationAssessmentAggregateResource that = (ApplicationAssessmentAggregateResource) o;

        return new EqualsBuilder()
                .append(totalScope, that.totalScope)
                .append(inScope, that.inScope)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(totalScope)
                .append(inScope)
                .toHashCode();
    }
}
