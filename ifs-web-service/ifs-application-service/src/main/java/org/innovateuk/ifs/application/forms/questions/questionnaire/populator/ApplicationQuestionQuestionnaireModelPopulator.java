package org.innovateuk.ifs.application.forms.questions.questionnaire.populator;

import org.innovateuk.ifs.application.forms.questions.questionnaire.viewmodel.ApplicationQuestionQuestionnaireViewModel;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.resource.QuestionStatusResource;
import org.innovateuk.ifs.application.service.ApplicationRestService;
import org.innovateuk.ifs.application.service.QuestionRestService;
import org.innovateuk.ifs.application.service.QuestionStatusRestService;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.form.resource.QuestionResource;
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

        Long questionnaireResponseId = questionnaireResponseLinkRestService.getResponseIdByApplicationIdAndOrganisationIdAndQuestionnaireId(question.getQuestionnaireId(), applicationId, organisationId).getSuccess();

        return new ApplicationQuestionQuestionnaireViewModel(
                application,
                question,
                organisationId,
                open,
                complete,
                questionnaireResponseId,
                answeredQuestionViewModelPopulator.allAnswers(questionnaireResponseId));

    }
}
