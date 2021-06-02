package org.innovateuk.ifs.assessment.overview.viewmodel;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.innovateuk.ifs.application.readonly.viewmodel.TermsAndConditionsRowReadOnlyViewModel;

import java.util.List;

/**
 * Holder of model attributes for the Assessment Overview view.
 */
public class AssessmentOverviewViewModel {

    private final long assessmentId;
    private final long applicationId;
    private final String applicationName;
    private final long competitionId;
    private final String competitionName;
    private final long daysLeftPercentage;
    private final long daysLeft;
    private final List<AssessmentOverviewSectionViewModel> sections;
    private final List<AssessmentOverviewAppendixViewModel> appendices;

    private final String termsAndConditionsTerminology;
    private final List<TermsAndConditionsRowReadOnlyViewModel> termsAndConditionsRows;
    private final boolean dualTermsAndConditions;

    public AssessmentOverviewViewModel(long assessmentId,
                                       long applicationId,
                                       String applicationName,
                                       long competitionId,
                                       String competitionName,
                                       long daysLeftPercentage,
                                       long daysLeft,
                                       List<AssessmentOverviewSectionViewModel> sections,
                                       List<AssessmentOverviewAppendixViewModel> appendices,
                                       String termsAndConditionsTerminology,
                                       List<TermsAndConditionsRowReadOnlyViewModel> termsAndConditionsRows,
                                       boolean dualTermsAndConditions) {
        this.assessmentId = assessmentId;
        this.applicationId = applicationId;
        this.applicationName = applicationName;
        this.competitionId = competitionId;
        this.competitionName = competitionName;
        this.daysLeftPercentage = daysLeftPercentage;
        this.daysLeft = daysLeft;
        this.sections = sections;
        this.appendices = appendices;
        this.termsAndConditionsTerminology = termsAndConditionsTerminology;
        this.termsAndConditionsRows = termsAndConditionsRows;
        this.dualTermsAndConditions = dualTermsAndConditions;
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

    public String getCompetitionName() {
        return competitionName;
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

    public String getTermsAndConditionsTerminology() {
        return termsAndConditionsTerminology;
    }

    public List<TermsAndConditionsRowReadOnlyViewModel> getTermsAndConditionsRows() {
        return termsAndConditionsRows;
    }

    public boolean isDualTermsAndConditions() {
        return dualTermsAndConditions;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        AssessmentOverviewViewModel that = (AssessmentOverviewViewModel) o;

        return new EqualsBuilder()
                .append(assessmentId, that.assessmentId)
                .append(applicationId, that.applicationId)
                .append(competitionId, that.competitionId)
                .append(daysLeftPercentage, that.daysLeftPercentage)
                .append(daysLeft, that.daysLeft)
                .append(dualTermsAndConditions, that.dualTermsAndConditions)
                .append(applicationName, that.applicationName)
                .append(competitionName, that.competitionName)
                .append(sections, that.sections)
                .append(appendices, that.appendices)
                .append(termsAndConditionsTerminology, that.termsAndConditionsTerminology)
                .append(termsAndConditionsRows, that.termsAndConditionsRows)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(assessmentId)
                .append(applicationId)
                .append(applicationName)
                .append(competitionId)
                .append(competitionName)
                .append(daysLeftPercentage)
                .append(daysLeft)
                .append(sections)
                .append(appendices)
                .append(termsAndConditionsTerminology)
                .append(termsAndConditionsRows)
                .append(dualTermsAndConditions)
                .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("assessmentId", assessmentId)
                .append("applicationId", applicationId)
                .append("applicationName", applicationName)
                .append("competitionId", competitionId)
                .append("competitionName", competitionName)
                .append("daysLeftPercentage", daysLeftPercentage)
                .append("daysLeft", daysLeft)
                .append("sections", sections)
                .append("appendices", appendices)
                .append("termsAndConditionsTerminology", termsAndConditionsTerminology)
                .append("termsAndConditionsRows", termsAndConditionsRows)
                .append("dualTermsAndConditions", dualTermsAndConditions)
                .toString();
    }
}
