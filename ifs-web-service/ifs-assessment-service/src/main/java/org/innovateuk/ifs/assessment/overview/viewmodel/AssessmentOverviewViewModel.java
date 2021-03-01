package org.innovateuk.ifs.assessment.overview.viewmodel;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.innovateuk.ifs.application.common.viewmodel.ApplicationSubsidyBasisViewModel;

import java.util.List;
import java.util.Objects;

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
    private final ApplicationSubsidyBasisViewModel applicationSubsidyBasisViewModel;

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
                                       ApplicationSubsidyBasisViewModel applicationSubsidyBasisViewModel) {
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
        this.applicationSubsidyBasisViewModel = applicationSubsidyBasisViewModel;
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

    public ApplicationSubsidyBasisViewModel getApplicationSubsidyBasisViewModel() {
        return applicationSubsidyBasisViewModel;
    }
}
