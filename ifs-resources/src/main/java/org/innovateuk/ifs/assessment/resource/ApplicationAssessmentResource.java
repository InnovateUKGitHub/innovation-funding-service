package org.innovateuk.ifs.assessment.resource;

import java.math.BigDecimal;
import java.util.Map;

/**
 * Aggregate assessor scores for an Application.
 */
public class ApplicationAssessmentResource {

    private long assessmentId;
    private long applicationId;
    private boolean inScope;
    private Map<Long, BigDecimal> scores;
    private Map<Long, String> feedback;
    private BigDecimal averagePercentage;

    public ApplicationAssessmentResource() {}

    public ApplicationAssessmentResource(long assessmentId, long applicationId, boolean inScope, Map<Long, BigDecimal> scores, Map<Long, String> feedback, BigDecimal averagePercentage) {
        this.assessmentId = assessmentId;
        this.applicationId = applicationId;
        this.inScope = inScope;
        this.scores = scores;
        this.feedback = feedback;
        this.averagePercentage = averagePercentage;
    }

    public long getAssessmentId() {
        return assessmentId;
    }

    public long getApplicationId() {
        return applicationId;
    }

    public boolean isInScope() {
        return inScope;
    }

    public Map<Long, BigDecimal> getScores() {
        return scores;
    }

    public Map<Long, String> getFeedback() {
        return feedback;
    }

    public BigDecimal getAveragePercentage() {
        return averagePercentage;
    }
}
