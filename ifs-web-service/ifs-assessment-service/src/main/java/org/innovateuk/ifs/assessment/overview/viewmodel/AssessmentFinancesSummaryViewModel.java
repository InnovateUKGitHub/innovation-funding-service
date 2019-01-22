package org.innovateuk.ifs.assessment.overview.viewmodel;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.innovateuk.ifs.competition.publiccontent.resource.FundingType;

/**
 * Holder of model attributes for the Assessment Finances Summary view.
 */
public class AssessmentFinancesSummaryViewModel {

    private long assessmentId;
    private long applicationId;
    private String applicationName;
    private long daysLeft;
    private long daysLeftPercentage;
    private boolean collaborativeProject;
    private FundingType fundingType;

    public AssessmentFinancesSummaryViewModel(long assessmentId, long applicationId, String applicationName, long daysLeft, long daysLeftPercentage, boolean collaborativeProject, FundingType fundingType) {
        this.assessmentId = assessmentId;
        this.applicationId = applicationId;
        this.applicationName = applicationName;
        this.daysLeft = daysLeft;
        this.daysLeftPercentage = daysLeftPercentage;
        this.collaborativeProject = collaborativeProject;
        this.fundingType = fundingType;
    }

    public long getAssessmentId() {
        return assessmentId;
    }

    public long getApplicationId() {
        return applicationId;
    }

    public String getApplicationName() {
        return applicationName;
    }

    public long getDaysLeft() {
        return daysLeft;
    }

    public long getDaysLeftPercentage() {
        return daysLeftPercentage;
    }

    public FundingType getFundingType() {
        return fundingType;
    }

    public boolean isCollaborativeProject() {
        return collaborativeProject;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        AssessmentFinancesSummaryViewModel that = (AssessmentFinancesSummaryViewModel) o;

        return new EqualsBuilder()
                .append(assessmentId, that.assessmentId)
                .append(applicationId, that.applicationId)
                .append(daysLeft, that.daysLeft)
                .append(daysLeftPercentage, that.daysLeftPercentage)
                .append(collaborativeProject, that.collaborativeProject)
                .append(applicationName, that.applicationName)
                .append(fundingType, that.fundingType)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(assessmentId)
                .append(applicationId)
                .append(applicationName)
                .append(daysLeft)
                .append(daysLeftPercentage)
                .append(collaborativeProject)
                .append(fundingType)
                .toHashCode();
    }
}