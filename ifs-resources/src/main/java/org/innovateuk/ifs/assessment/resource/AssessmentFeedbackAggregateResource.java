package org.innovateuk.ifs.assessment.resource;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.math.BigDecimal;
import java.util.List;

/**
 * Response class for per question assessor feedback
 */
public class AssessmentFeedbackAggregateResource {

    private BigDecimal avgScore;
    private List<String> feedback;

    public AssessmentFeedbackAggregateResource() {
    }

    public AssessmentFeedbackAggregateResource(BigDecimal avgScore, List<String> feedback) {
        this.avgScore = avgScore;
        this.feedback = feedback;
    }

    public BigDecimal getAvgScore() {
        return avgScore;
    }

    public List<String> getFeedback() {
        return feedback;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        AssessmentFeedbackAggregateResource that = (AssessmentFeedbackAggregateResource) o;

        return new EqualsBuilder()
                .append(avgScore, that.avgScore)
                .append(feedback, that.feedback)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(avgScore)
                .append(feedback)
                .toHashCode();
    }
}
