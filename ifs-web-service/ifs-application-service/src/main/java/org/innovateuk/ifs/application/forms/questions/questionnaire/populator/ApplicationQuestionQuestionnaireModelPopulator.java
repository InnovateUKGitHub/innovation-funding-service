package org.innovateuk.ifs.application.forms.questions.questionnaire.populator;

import org.innovateuk.ifs.application.forms.questions.questionnaire.viewmodel.ApplicationQuestionQuestionnaireViewModel;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.resource.QuestionStatusResource;
import org.innovateuk.ifs.application.service.ApplicationRestService;
import org.innovateuk.ifs.application.service.QuestionRestService;
import org.innovateuk.ifs.application.service.QuestionStatusRestService;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.finance.service.ApplicationFinanceRestService;
import org.innovateuk.ifs.form.resource.QuestionResource;
import org.innovateuk.ifs.question.resource.QuestionSetupType;
import org.innovateuk.ifs.questionnaire.link.service.QuestionnaireResponseLinkRestService;
import org.innovateuk.ifs.questionnaire.response.populator.AnsweredQuestionViewModelPopulator;
import org.innovateuk.ifs.user.resource.Role;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.service.ProcessRoleRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ApplicationQuestionQuestionnaireModelPopulator {

    @Autowired
    private CompetitionRestService competitionRestService;

    @Autowired
    private ApplicationRestService applicationRestService;

    @Autowired
    private ProcessRoleRestService processRoleRestService;

    @Autowired
    private QuestionStatusRestService questionStatusRestService;

    @Autowired
    private AnsweredQuestionViewModelPopulator answeredQuestionViewModelPopulator;

    @Autowired
    private QuestionnaireResponseLinkRestService questionnaireResponseLinkRestService;

    @Autowired
    private QuestionRestService questionRestService;

    @Autowired
    private ApplicationFinanceRestService applicationFinanceRestService;

    public ApplicationQuestionQuestionnaireViewModel populate(UserResource user,
                                                              long applicationId,
                                                              long questionId,
                                                              long organisationId) {
        ApplicationResource application = applicationRestService.getApplicationById(applicationId).getSuccess();
        CompetitionResource competition = competitionRestService.getCompetitionById(application.getCompetition()).getSuccess();
        QuestionResource question = questionRestService.findById(questionId).getSuccess();

        boolean userCanEdit = user.hasRole(Role.APPLICANT) && processRoleRestService.findProcessRole(user.getId(), applicationId).getOptionalSuccessObject()
                .map(role -> role.getOrganisationId() != null && role.getOrganisationId().equals(organisationId))
                .orElse(false);
        boolean open = userCanEdit && application.isOpen() && competition.isOpen();

        boolean complete = questionStatusRestService.getMarkedAsCompleteByQuestionApplicationAndOrganisation(
                question.getId(), applicationId, organisationId)
                .getSuccess()
                .map(QuestionStatusResource::getMarkedAsComplete)
                .orElse(false);

        String questionnaireResponseId = questionnaireResponseLinkRestService.getResponseIdByApplicationIdAndOrganisationIdAndQuestionnaireId(question.getQuestionnaireId(), applicationId, organisationId)
                .getSuccess()
                .getContent();

        Boolean northernIrelandDeclaration = null;
        if (question.getQuestionSetupType() == QuestionSetupType.SUBSIDY_BASIS) {
            northernIrelandDeclaration = applicationFinanceRestService.getApplicationFinance(applicationId, organisationId).getSuccess().getNorthernIrelandDeclaration();
        }

        return new ApplicationQuestionQuestionnaireViewModel(
                application,
                question,
                organisationId,
                open,
                complete,
                northernIrelandDeclaration,
                questionnaireResponseId,
                answeredQuestionViewModelPopulator.allAnswers(questionnaireResponseId, complete || !open));

    }
}
