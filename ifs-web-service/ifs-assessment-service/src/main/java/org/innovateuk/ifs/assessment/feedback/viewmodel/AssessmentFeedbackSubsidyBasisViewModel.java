package org.innovateuk.ifs.assessment.feedback.viewmodel;

import org.innovateuk.ifs.application.common.viewmodel.ApplicationSubsidyBasisViewModel;

public class AssessmentFeedbackSubsidyBasisViewModel extends BaseAssessmentFeedbackViewModel {

    private String applicationName;
    private long daysLeft;
    private long daysLeftPercentage;
    private String questionShortName;
    private ApplicationSubsidyBasisViewModel applicationSubsidyBasisViewModel;

    public AssessmentFeedbackSubsidyBasisViewModel(String applicationName,
                                                   long daysLeft,
                                                   long daysLeftPercentage,
                                                   String questionShortName,
                                                   ApplicationSubsidyBasisViewModel applicationSubsidyBasisViewModel) {
        this.applicationName = applicationName;
        this.daysLeft = daysLeft;
        this.daysLeftPercentage = daysLeftPercentage;
        this.questionShortName = questionShortName;
        this.applicationSubsidyBasisViewModel = applicationSubsidyBasisViewModel;
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

    public ApplicationSubsidyBasisViewModel getApplicationSubsidyBasisViewModel() {
        return applicationSubsidyBasisViewModel;
    }

    public String getQuestionShortName() {
        return questionShortName;
    }

}