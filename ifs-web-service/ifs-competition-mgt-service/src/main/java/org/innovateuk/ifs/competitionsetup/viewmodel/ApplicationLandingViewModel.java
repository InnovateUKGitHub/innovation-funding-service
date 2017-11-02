package org.innovateuk.ifs.competitionsetup.viewmodel;

import org.innovateuk.ifs.application.resource.QuestionResource;
import org.innovateuk.ifs.competitionsetup.viewmodel.fragments.GeneralSetupViewModel;
import org.springframework.util.CollectionUtils;

import java.util.List;

public class ApplicationLandingViewModel extends CompetitionSetupViewModel {
    private List<QuestionResource> questions;
    private List<QuestionResource> projectDetails;

    public ApplicationLandingViewModel(GeneralSetupViewModel generalSetupViewModel, List<QuestionResource> questions, List<QuestionResource> projectDetails) {
        this.generalSetupViewModel = generalSetupViewModel;
        this.questions = questions;
        this.projectDetails = projectDetails;
    }

    public List<QuestionResource> getQuestions() {
        return questions;
    }

    public List<QuestionResource> getProjectDetails() {
        return projectDetails;
    }

    public boolean multipleAssessedQuestionsLeft() {
        return !CollectionUtils.isEmpty(questions) && questions.size() > 1;
    }
}
