package org.innovateuk.ifs.application.forms.questions.questionnaire.viewmodel;

import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.form.resource.QuestionResource;
import org.innovateuk.ifs.questionnaire.response.viewmodel.AnswerTableViewModel;

public class ApplicationQuestionQuestionnaireViewModel extends AbstractQuestionQuestionnaireViewModel {
    private final long applicationId;
    private final String applicationName;

    public ApplicationQuestionQuestionnaireViewModel(ApplicationResource application, QuestionResource question, long organisationId, boolean open, boolean complete, Boolean northernIrelandDeclaration, String questionnaireResponseId, AnswerTableViewModel answers) {
        super(question, organisationId, open, complete, northernIrelandDeclaration, questionnaireResponseId, answers);
        this.applicationId = application.getId();
        this.applicationName = application.getName();
    }

    public long getApplicationId() {
        return applicationId;
    }

    public String getApplicationName() {
        return applicationName;
    }
}
