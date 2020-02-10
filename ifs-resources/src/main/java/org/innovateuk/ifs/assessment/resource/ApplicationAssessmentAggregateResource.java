package org.innovateuk.ifs.assessment.resource;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.math.BigDecimal;
import java.util.Map;

/**
 * Aggregate assessor scores for an Application.
 */
public class ApplicationAssessmentAggregateResource {

    private boolean scopeAssessed;
    private int totalScope;
    private int inScope;
    private Map<Long, BigDecimal> scores;
    private BigDecimal averagePercentage;

    public ApplicationAssessmentAggregateResource() {
    }

    public ApplicationAssessmentAggregateResource(boolean scopeAssessed, int totalScope, int inScope, Map<Long, BigDecimal> scores, BigDecimal averagePercentage) {
        this.scopeAssessed = scopeAssessed;
        this.totalScope = totalScope;
        this.inScope = inScope;
        this.scores = scores;
        this.averagePercentage = averagePercentage;
    }

    public boolean isScopeAssessed() {
        return scopeAssessed;
    }

    public int getTotalScope() {
        return totalScope;
    }

    public int getInScope() {
        return inScope;
    }

    public Map<Long, BigDecimal> getScores() {
        return scores;
    }

    public BigDecimal getAveragePercentage() {
        return averagePercentage;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final ApplicationAssessmentAggregateResource that = (ApplicationAssessmentAggregateResource) o;

        return new EqualsBuilder()
                .append(scopeAssessed, that.scopeAssessed)
                .append(totalScope, that.totalScope)
                .append(inScope, that.inScope)
                .append(averagePercentage, that.averagePercentage)
                .append(scores, that.scores)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(scopeAssessed)
                .append(totalScope)
                .append(inScope)
                .append(scores)
                .append(averagePercentage)
                .toHashCode();
    }
}
