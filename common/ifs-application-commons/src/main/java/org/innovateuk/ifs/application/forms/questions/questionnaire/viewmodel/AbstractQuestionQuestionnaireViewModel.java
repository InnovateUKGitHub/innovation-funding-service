package org.innovateuk.ifs.application.forms.questions.questionnaire.viewmodel;

import org.innovateuk.ifs.form.resource.QuestionResource;
import org.innovateuk.ifs.questionnaire.response.viewmodel.AnswerTableViewModel;
import org.innovateuk.ifs.questionnaire.response.viewmodel.AnsweredQuestionViewModel;

import java.util.Objects;

public abstract class AbstractQuestionQuestionnaireViewModel {
    private final long questionId;
    private final String questionName;
    private final long organisationId;
    private final boolean open;
    private final boolean complete;
    private final Boolean northernIrelandDeclaration;
    private final String questionnaireResponseId;
    private final AnswerTableViewModel answers;

    public AbstractQuestionQuestionnaireViewModel(QuestionResource question, long organisationId, boolean open, boolean complete, Boolean northernIrelandDeclaration, String questionnaireResponseId, AnswerTableViewModel answers) {
        this.questionId = question.getId();
        this.questionName = question.getName();
        this.organisationId = organisationId;
        this.open = open;
        this.complete = complete;
        this.northernIrelandDeclaration = northernIrelandDeclaration;
        this.questionnaireResponseId = questionnaireResponseId;
        this.answers = answers;
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

    public Boolean getNorthernIrelandDeclaration() {
        return northernIrelandDeclaration;
    }

    public String getQuestionnaireResponseId() {
        return questionnaireResponseId;
    }

    public AnswerTableViewModel getAnswers() {
        return answers;
    }

    public boolean isReadOnly() {
        return complete || !open;
    }

    public boolean navigateStraightToQuestionnaireWelcome() {
        return !isReadOnly() && getAnswers().getQuestions().isEmpty();
    }

    public boolean allQuestionsAnswered() {
        return !getAnswers().getQuestions().isEmpty()
                && getAnswers().getQuestions().stream()
                .map(AnsweredQuestionViewModel::getAnswer)
                .noneMatch(Objects::isNull);
    }


}
