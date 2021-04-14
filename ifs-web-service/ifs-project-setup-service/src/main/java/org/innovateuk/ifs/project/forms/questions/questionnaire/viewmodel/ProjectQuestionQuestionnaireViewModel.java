package org.innovateuk.ifs.project.forms.questions.questionnaire.viewmodel;

import org.innovateuk.ifs.application.forms.questions.questionnaire.viewmodel.AbstractQuestionQuestionnaireViewModel;
import org.innovateuk.ifs.form.resource.QuestionResource;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.questionnaire.response.viewmodel.AnswerTableViewModel;

public class ProjectQuestionQuestionnaireViewModel extends AbstractQuestionQuestionnaireViewModel {

    private final long projectId;
    private final String projectName;

    public ProjectQuestionQuestionnaireViewModel(ProjectResource project, QuestionResource question, long organisationId, boolean open, boolean complete, Boolean northernIrelandDeclaration, String questionnaireResponseId, AnswerTableViewModel answers) {
        super(question, organisationId, open, complete, northernIrelandDeclaration, questionnaireResponseId, answers);
        this.projectId = project.getId();
        this.projectName = project.getName();
    }

    public long getProjectId() {
        return projectId;
    }

    public String getProjectName() {
        return projectName;
    }
}
