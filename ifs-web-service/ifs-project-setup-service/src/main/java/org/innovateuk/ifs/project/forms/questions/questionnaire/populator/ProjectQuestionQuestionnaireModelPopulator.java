package org.innovateuk.ifs.project.forms.questions.questionnaire.populator;

import org.innovateuk.ifs.application.service.QuestionRestService;
import org.innovateuk.ifs.form.resource.QuestionResource;
import org.innovateuk.ifs.project.ProjectService;
import org.innovateuk.ifs.project.finance.service.ProjectFinanceRestService;
import org.innovateuk.ifs.project.forms.questions.questionnaire.viewmodel.ProjectQuestionQuestionnaireViewModel;
import org.innovateuk.ifs.project.projectteam.PendingPartnerProgressRestService;
import org.innovateuk.ifs.project.resource.PendingPartnerProgressResource;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.question.resource.QuestionSetupType;
import org.innovateuk.ifs.questionnaire.link.service.QuestionnaireResponseLinkRestService;
import org.innovateuk.ifs.questionnaire.response.populator.AnsweredQuestionViewModelPopulator;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ProjectQuestionQuestionnaireModelPopulator {

    @Autowired
    private ProjectService projectService;

    @Autowired
    private PendingPartnerProgressRestService pendingPartnerProgressRestService;

    @Autowired
    private AnsweredQuestionViewModelPopulator answeredQuestionViewModelPopulator;

    @Autowired
    private QuestionnaireResponseLinkRestService questionnaireResponseLinkRestService;

    @Autowired
    private QuestionRestService questionRestService;

    @Autowired
    private ProjectFinanceRestService projectFinanceRestService;

    public ProjectQuestionQuestionnaireViewModel populate(UserResource user,
                                                          long projectId,
                                                          long questionId,
                                                          long organisationId) {
        ProjectResource project = projectService.getById(projectId);
        QuestionResource question = questionRestService.findById(questionId).getSuccess();
        PendingPartnerProgressResource progress = pendingPartnerProgressRestService.getPendingPartnerProgress(projectId, organisationId).getSuccess();

        boolean userCanEdit = projectService.getPartners(projectId).stream()
                .filter(partner -> user.getId().equals(partner.getUser()))
                .filter(partner -> partner.getOrganisation().equals(organisationId))
                .findFirst()
                .isPresent();

        boolean open = userCanEdit && !progress.isCompleted();

        boolean complete = progress.isSubsidyBasisComplete();

        String questionnaireResponseId = questionnaireResponseLinkRestService.getResponseIdByProjectIdAndQuestionnaireIdAndOrganisationId(projectId, question.getQuestionnaireId(), organisationId)
                .getSuccess()
                .getContent();

        Boolean northernIrelandDeclaration = null;
        if (question.getQuestionSetupType() == QuestionSetupType.SUBSIDY_BASIS) {
            northernIrelandDeclaration = projectFinanceRestService.getProjectFinance(projectId, organisationId).getSuccess().getNorthernIrelandDeclaration();
        }

        return new ProjectQuestionQuestionnaireViewModel(
                project,
                question,
                organisationId,
                open,
                complete,
                northernIrelandDeclaration,
                questionnaireResponseId,
                answeredQuestionViewModelPopulator.allAnswers(questionnaireResponseId, complete || !open));
    }
}
