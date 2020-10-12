package org.innovateuk.ifs.management.competition.setup.application.viewmodel;

import org.innovateuk.ifs.competition.resource.CompetitionSetupSubsection;
import org.innovateuk.ifs.management.competition.setup.core.viewmodel.CompetitionSetupViewModel;
import org.innovateuk.ifs.management.competition.setup.core.viewmodel.GeneralSetupViewModel;
import org.innovateuk.ifs.form.resource.QuestionResource;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;

public class LandingViewModel extends CompetitionSetupViewModel {
    private List<QuestionResource> questions;
    private List<QuestionResource> projectDetails;
    private Map<CompetitionSetupSubsection, Boolean> subsectionStatuses;
    private Map<Long, Boolean> questionStatuses;
    private Boolean allComplete;
    private List<QuestionResource> ktpAssessorQuestions;


    public LandingViewModel(GeneralSetupViewModel generalSetupViewModel, List<QuestionResource> questions, List<QuestionResource> projectDetails,
                            Map<CompetitionSetupSubsection, Boolean> subsectionStatuses,
                            Map<Long, Boolean> questionStatuses,
                            Boolean allComplete,
                            List<QuestionResource> ktpAssessorQuestions) {
        this.generalSetupViewModel = generalSetupViewModel;
        this.questions = questions;
        this.projectDetails = projectDetails;
        this.subsectionStatuses = subsectionStatuses;
        this.questionStatuses = questionStatuses;
        this.allComplete = allComplete;
        this.ktpAssessorQuestions = ktpAssessorQuestions;
    }

    public List<QuestionResource> getKtpAssessorQuestions() {
        return ktpAssessorQuestions;
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
