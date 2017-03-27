package org.innovateuk.ifs.assessment.resource;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.math.BigDecimal;
import java.util.Map;

/**
 * Aggregate assessor scores for an Application.
 */
public class ApplicationAssessmentAggregateResource {

    private int totalScope;
    private int inScope;
    private Map<Long, BigDecimal> scores;
    private long averagePercentage;

    public ApplicationAssessmentAggregateResource() {
    }

    public ApplicationAssessmentAggregateResource(int totalScope, int inScope, Map<Long, BigDecimal> scores, long averagePercentage) {
        this.totalScope = totalScope;
        this.inScope = inScope;
        this.scores = scores;
        this.averagePercentage = averagePercentage;
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

    public long getAveragePercentage() {
        return averagePercentage;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        ApplicationAssessmentAggregateResource that = (ApplicationAssessmentAggregateResource) o;

        return new EqualsBuilder()
                .append(totalScope, that.totalScope)
                .append(inScope, that.inScope)
                .append(averagePercentage, that.averagePercentage)
                .append(scores, that.scores)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(totalScope)
                .append(inScope)
                .append(scores)
                .append(averagePercentage)
                .toHashCode();
    }
}
