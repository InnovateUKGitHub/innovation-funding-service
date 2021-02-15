package org.innovateuk.ifs.application.forms.questions.questionnaire.viewmodel;

import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.form.resource.QuestionResource;
import org.innovateuk.ifs.questionnaire.response.viewmodel.AnsweredQuestionViewModel;

import java.util.List;
import java.util.Objects;

public class ApplicationQuestionQuestionnaireViewModel {
    private final long applicationId;
    private final String applicationName;
    private final long questionId;
    private final String questionName;
    private final long organisationId;
    private final boolean open;
    private final boolean complete;
    private final long questionnaireResponseId;
    private final List<AnsweredQuestionViewModel> answers;

    public ApplicationQuestionQuestionnaireViewModel(ApplicationResource application, QuestionResource question, long organisationId, boolean open, boolean complete, long questionnaireResponseId, List<AnsweredQuestionViewModel> answers) {
        this.applicationId = application.getId();
        this.applicationName = application.getName();
        this.questionId = question.getId();
        this.questionName = question.getName();
        this.organisationId = organisationId;
        this.open = open;
        this.complete = complete;
        this.questionnaireResponseId = questionnaireResponseId;
        this.answers = answers;
    }

    public long getApplicationId() {
        return applicationId;
    }

    public String getApplicationName() {
        return applicationName;
    }

    public long getQuestionId() {
        return questionId;
    }

    public String getQuestionName() {
        return questionName;
    }

    public long getOrganisationId() {
        return organisationId;
    }

    public boolean isOpen() {
        return open;
    }

    public boolean isComplete() {
        return complete;
    }

    public long getQuestionnaireResponseId() {
        return questionnaireResponseId;
    }

    public List<AnsweredQuestionViewModel> getAnswers() {
        return answers;
    }

    public boolean isReadOnly() {
        return complete || !open;
    }

    public boolean navigateStraightToQuestionnaireWelcome() {
        return !isReadOnly() && getAnswers().isEmpty();
    }

    public boolean allQuestionsAnswered() {
        return !getAnswers().isEmpty()
                && getAnswers().stream()
                .map(AnsweredQuestionViewModel::getAnswer)
                .noneMatch(Objects::isNull);
    }
}
