package org.innovateuk.ifs.assessment.overview.viewmodel;

public class AssessmentTermsAndConditionsViewModel {

    private final long assessmentId;
    private final String competitionTermsTemplate;
    private final long daysLeft;
    private final long daysLeftPercentage;

    public AssessmentTermsAndConditionsViewModel(long assessmentId,
                                                 String competitionTermsTemplate,
                                                 long daysLeft,
                                                 long daysLeftPercentage) {
        this.assessmentId = assessmentId;
        this.competitionTermsTemplate = competitionTermsTemplate;
        this.daysLeft = daysLeft;
        this.daysLeftPercentage = daysLeftPercentage;
    }

    public long getAssessmentId() {
        return assessmentId;
    }

    public String getCompetitionTermsTemplate() {
        return competitionTermsTemplate;
    }

    public long getDaysLeft() {
        return daysLeft;
    }

    public long getDaysLeftPercentage() {
        return daysLeftPercentage;
    }
}