package org.innovateuk.ifs.assessment.overview.viewmodel;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.List;

/**
 * Holder of model attributes for the Assessment Overview view.
 */
public class AssessmentOverviewViewModel {

    private long assessmentId;
    private long applicationId;
    private String applicationName;
    private long competitionId;
    private long daysLeftPercentage;
    private long daysLeft;
    private List<AssessmentOverviewSectionViewModel> sections;
    private List<AssessmentOverviewAppendixViewModel> appendices;

    public AssessmentOverviewViewModel(long assessmentId,
                                       long applicationId,
                                       String applicationName,
                                       long competitionId,
                                       long daysLeftPercentage,
                                       long daysLeft,
                                       List<AssessmentOverviewSectionViewModel> sections,
                                       List<AssessmentOverviewAppendixViewModel> appendices) {
        this.assessmentId = assessmentId;
        this.applicationId = applicationId;
        this.applicationName = applicationName;
        this.competitionId = competitionId;
        this.daysLeftPercentage = daysLeftPercentage;
        this.daysLeft = daysLeft;
        this.sections = sections;
        this.appendices = appendices;
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

    public long getCompetitionId() {
        return competitionId;
    }

    public long getDaysLeftPercentage() {
        return daysLeftPercentage;
    }

    public long getDaysLeft() {
        return daysLeft;
    }

    public List<AssessmentOverviewSectionViewModel> getSections() {
        return sections;
    }

    public List<AssessmentOverviewAppendixViewModel> getAppendices() {
        return appendices;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        AssessmentOverviewViewModel that = (AssessmentOverviewViewModel) o;

        return new EqualsBuilder()
                .append(assessmentId, that.assessmentId)
                .append(applicationId, that.applicationId)
                .append(competitionId, that.competitionId)
                .append(daysLeftPercentage, that.daysLeftPercentage)
                .append(daysLeft, that.daysLeft)
                .append(applicationName, that.applicationName)
                .append(sections, that.sections)
                .append(appendices, that.appendices)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(assessmentId)
                .append(applicationId)
                .append(applicationName)
                .append(competitionId)
                .append(daysLeftPercentage)
                .append(daysLeft)
                .append(sections)
                .append(appendices)
                .toHashCode();
    }
}
