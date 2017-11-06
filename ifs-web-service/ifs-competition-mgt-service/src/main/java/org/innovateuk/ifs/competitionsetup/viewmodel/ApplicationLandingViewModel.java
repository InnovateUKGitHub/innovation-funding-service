package org.innovateuk.ifs.competitionsetup.viewmodel;

import org.innovateuk.ifs.application.resource.QuestionResource;
import org.innovateuk.ifs.competition.resource.CompetitionSetupSubsection;
import org.innovateuk.ifs.competitionsetup.viewmodel.fragments.GeneralSetupViewModel;

import java.util.List;
import java.util.Map;

public class ApplicationLandingViewModel extends CompetitionSetupViewModel {
    private List<QuestionResource> questions;
    private List<QuestionResource> projectDetails;
    private Map<CompetitionSetupSubsection, Boolean> subsectionStatuses;
    private Map<Long, Boolean> questionStatuses;
    private Boolean allComplete;

    public ApplicationLandingViewModel(GeneralSetupViewModel generalSetupViewModel, List<QuestionResource> questions, List<QuestionResource> projectDetails,
                                       Map<CompetitionSetupSubsection, Boolean> subsectionStatuses,
                                       Map<Long, Boolean> questionStatuses,
                                       Boolean allComplete) {
        this.generalSetupViewModel = generalSetupViewModel;
        this.questions = questions;
        this.projectDetails = projectDetails;
        this.subsectionStatuses = subsectionStatuses;
        this.questionStatuses = questionStatuses;
        this.allComplete = allComplete;
    }

    public List<QuestionResource> getQuestions() {
        return questions;
    }

    public List<QuestionResource> getProjectDetails() {
        return projectDetails;
    }

    public Map<CompetitionSetupSubsection, Boolean> getSubsectionStatuses() {
        return subsectionStatuses;
    }

    public Map<Long, Boolean> getQuestionStatuses() {
        return questionStatuses;
    }

    public Boolean getAllComplete() {
        return allComplete;
    }
}
