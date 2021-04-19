package org.innovateuk.ifs.project.fundingrules.populator;

import org.innovateuk.ifs.application.service.QuestionRestService;
import org.innovateuk.ifs.commons.exception.ObjectNotFoundException;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.form.resource.QuestionResource;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.project.ProjectService;
import org.innovateuk.ifs.project.finance.resource.FundingRulesResource;
import org.innovateuk.ifs.project.finance.service.FinanceCheckRestService;
import org.innovateuk.ifs.project.fundingrules.viewmodel.FinanceChecksFundingRulesViewModel;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.question.resource.QuestionSetupType;
import org.innovateuk.ifs.questionnaire.link.service.QuestionnaireResponseLinkRestService;
import org.innovateuk.ifs.questionnaire.response.populator.AnsweredQuestionViewModelPopulator;
import org.innovateuk.ifs.questionnaire.response.viewmodel.AnswerTableViewModel;
import org.innovateuk.ifs.string.resource.StringResource;
import org.innovateuk.ifs.user.resource.ProcessRoleResource;
import org.innovateuk.ifs.user.service.OrganisationRestService;
import org.innovateuk.ifs.user.service.ProcessRoleRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class FinanceChecksFundingRulesViewModelPopulator {

    @Autowired
    private ProjectService projectService;

    @Autowired
    private CompetitionRestService competitionRestService;

    @Autowired
    private OrganisationRestService organisationRestService;

    @Autowired
    private FinanceCheckRestService financeCheckRestService;

    @Autowired
    private AnsweredQuestionViewModelPopulator answeredQuestionViewModelPopulator;

    @Autowired
    private QuestionRestService questionRestService;

    @Autowired
    private ProcessRoleRestService processRoleRestService;

    @Autowired
    private QuestionnaireResponseLinkRestService questionnaireResponseLinkRestService;

    public FinanceChecksFundingRulesViewModel populateFundingRulesViewModel(Long projectId, Long organisationId, boolean editMode) {

        ProjectResource project = projectService.getById(projectId);
        CompetitionResource competition = competitionRestService.getCompetitionById(project.getCompetition()).getSuccess();
        OrganisationResource organisation = organisationRestService.getOrganisationById(organisationId).getSuccess();
        FundingRulesResource fundingRulesResource = financeCheckRestService.getFundingRules(projectId, organisationId).getSuccess();

        OrganisationResource leadOrganisation = projectService.getLeadOrganisation(projectId);

        boolean leadPartnerOrganisation = leadOrganisation.getId().equals(organisation.getId());

        AnswerTableViewModel questionsAndAnswers = questionsAndAnswers(project, organisationId);

        return new FinanceChecksFundingRulesViewModel(project,
                competition,
                organisation,
                leadPartnerOrganisation,
                fundingRulesResource,
                questionsAndAnswers,
                editMode);
    }

    private AnswerTableViewModel questionsAndAnswers(ProjectResource project, Long organisationId) {

        String questionnaireResponseId = questionnaireResponseId(project, organisationId);

        if (questionnaireResponseId == null) {
            return null;
        }

        return answeredQuestionViewModelPopulator.allAnswers(questionnaireResponseId, null, true);
    }

    private String questionnaireResponseId(ProjectResource project, Long organisationId) {

        QuestionResource subsidyBasisQuestion = subsidyBasisQuestion(project.getCompetition());

        if (subsidyBasisQuestion == null || subsidyBasisQuestion.getQuestionnaireId() == null) {
            return null;
        }

        List<ProcessRoleResource> applicationProcessRoles = processRoleRestService.findProcessRole(project.getApplication()).getSuccess();
        boolean orgPresentOnApplication = applicationProcessRoles.stream().anyMatch(apr ->
                apr.getOrganisationId() != null && apr.getOrganisationId().equals(organisationId)
        );

        if (orgPresentOnApplication) {
            return questionnaireResponseIdFromApplication(subsidyBasisQuestion.getQuestionnaireId(), project.getApplication(), organisationId);
        }
        return questionnaireResponseIdFromProject(subsidyBasisQuestion.getQuestionnaireId(), project.getId(), organisationId);
    }

    private String questionnaireResponseIdFromApplication(long questionnaireId, long applicationId, long organisationId) {
        StringResource response;
        try {
            response = questionnaireResponseLinkRestService.getResponseIdByApplicationIdAndOrganisationIdAndQuestionnaireId(questionnaireId, applicationId, organisationId).getSuccess();
        } catch (ObjectNotFoundException e) {
            // questionnaire does not exist
            return null;
        }
        return response.getContent();
    }

    private String questionnaireResponseIdFromProject(long questionnaireId, long projectId, long organisationId) {
        StringResource response;
        try {
            response = questionnaireResponseLinkRestService.getResponseIdByProjectIdAndQuestionnaireIdAndOrganisationId(projectId, questionnaireId, organisationId).getSuccess();
        } catch (ObjectNotFoundException e) {
            // questionnaire does not exist
            return null;
        }
        return response.getContent();
    }

    private QuestionResource subsidyBasisQuestion(Long competitionId) {
        try {
            return questionRestService.getQuestionByCompetitionIdAndQuestionSetupType(competitionId, QuestionSetupType.SUBSIDY_BASIS).getSuccess();
        } catch (ObjectNotFoundException e) {
            // question does not exist
            return null;
        }
    }
}
