package org.innovateuk.ifs.assessment.overview.viewmodel;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.innovateuk.ifs.application.readonly.viewmodel.TermsAndConditionsRowReadOnlyViewModel;
import org.innovateuk.ifs.competition.resource.CompetitionThirdPartyConfigResource;

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
    private final boolean alwaysOpen;
    private final Long assessmentPeriodId;
    private final List<AssessmentOverviewSectionViewModel> sections;
    private final List<AssessmentOverviewAppendixViewModel> appendices;

    private final String termsAndConditionsTerminology;
    private final List<TermsAndConditionsRowReadOnlyViewModel> termsAndConditionsRows;
    private final boolean dualTermsAndConditions;
    private final boolean thirdPartyProcurement;
    private final CompetitionThirdPartyConfigResource thirdPartyConfig;

    public AssessmentOverviewViewModel(long assessmentId,
                                       long applicationId,
                                       String applicationName,
                                       long competitionId,
                                       String competitionName,
                                       long daysLeftPercentage,
                                       long daysLeft,
                                       boolean alwaysOpen,
                                       Long assessmentPeriodId,
                                       List<AssessmentOverviewSectionViewModel> sections,
                                       List<AssessmentOverviewAppendixViewModel> appendices,
                                       String termsAndConditionsTerminology,
                                       List<TermsAndConditionsRowReadOnlyViewModel> termsAndConditionsRows,
                                       boolean dualTermsAndConditions,
                                       boolean thirdPartyProcurement,
                                       CompetitionThirdPartyConfigResource thirdPartyConfig) {
        this.assessmentId = assessmentId;
        this.applicationId = applicationId;
        this.applicationName = applicationName;
        this.competitionId = competitionId;
        this.competitionName = competitionName;
        this.daysLeftPercentage = daysLeftPercentage;
        this.daysLeft = daysLeft;
        this.alwaysOpen = alwaysOpen;
        this.assessmentPeriodId = assessmentPeriodId;
        this.sections = sections;
        this.appendices = appendices;
        this.termsAndConditionsTerminology = termsAndConditionsTerminology;
        this.termsAndConditionsRows = termsAndConditionsRows;
        this.dualTermsAndConditions = dualTermsAndConditions;
        this.thirdPartyProcurement = thirdPartyProcurement;
        this.thirdPartyConfig = thirdPartyConfig;
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

    public boolean isThirdPartyProcurement() { return thirdPartyProcurement; }

    public CompetitionThirdPartyConfigResource getThirdPartyConfig() { return thirdPartyConfig; }

    public boolean isAlwaysOpen() { return alwaysOpen; }

    public Long getAssessmentPeriodId() { return assessmentPeriodId; }

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
                .append(alwaysOpen, that.alwaysOpen)
                .append(assessmentPeriodId, that.assessmentPeriodId)
                .append(dualTermsAndConditions, that.dualTermsAndConditions)
                .append(applicationName, that.applicationName)
                .append(competitionName, that.competitionName)
                .append(sections, that.sections)
                .append(appendices, that.appendices)
                .append(termsAndConditionsTerminology, that.termsAndConditionsTerminology)
                .append(termsAndConditionsRows, that.termsAndConditionsRows)
                .append(thirdPartyConfig, that.thirdPartyConfig)
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
                .append(alwaysOpen)
                .append(assessmentPeriodId)
                .append(sections)
                .append(appendices)
                .append(termsAndConditionsTerminology)
                .append(termsAndConditionsRows)
                .append(dualTermsAndConditions)
                .append(thirdPartyConfig)
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
                .append("alwaysOpen", alwaysOpen)
                .append("assessmentPeriodId", assessmentPeriodId)
                .append("sections", sections)
                .append("appendices", appendices)
                .append("termsAndConditionsTerminology", termsAndConditionsTerminology)
                .append("termsAndConditionsRows", termsAndConditionsRows)
                .append("dualTermsAndConditions", dualTermsAndConditions)
                .append("thirdPartyConfig", thirdPartyConfig)
                .toString();
    }
}
