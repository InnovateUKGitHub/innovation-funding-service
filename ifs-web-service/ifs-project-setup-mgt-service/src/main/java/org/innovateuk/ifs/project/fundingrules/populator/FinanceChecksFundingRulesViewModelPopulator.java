package org.innovateuk.ifs.project.fundingrules.populator;

import org.innovateuk.ifs.application.service.QuestionRestService;
import org.innovateuk.ifs.commons.exception.ObjectNotFoundException;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.FundingRules;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.form.resource.QuestionResource;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.project.ProjectService;
import org.innovateuk.ifs.project.finance.service.ProjectFinanceRestService;
import org.innovateuk.ifs.project.fundingrules.viewmodel.FinanceChecksFundingRulesViewModel;
import org.innovateuk.ifs.project.fundingrules.viewmodel.QuestionnaireQuestionAnswerViewModel;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.question.resource.QuestionSetupType;
import org.innovateuk.ifs.questionnaire.config.service.QuestionnaireOptionRestService;
import org.innovateuk.ifs.questionnaire.config.service.QuestionnaireQuestionRestService;
import org.innovateuk.ifs.questionnaire.config.service.QuestionnaireRestService;
import org.innovateuk.ifs.questionnaire.link.service.QuestionnaireResponseLinkRestService;
import org.innovateuk.ifs.questionnaire.resource.*;
import org.innovateuk.ifs.questionnaire.response.service.QuestionnaireQuestionResponseRestService;
import org.innovateuk.ifs.questionnaire.response.service.QuestionnaireResponseRestService;
import org.innovateuk.ifs.string.resource.StringResource;
import org.innovateuk.ifs.user.service.OrganisationRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class FinanceChecksFundingRulesViewModelPopulator {

    @Autowired
    private ProjectService projectService;

    @Autowired
    private CompetitionRestService competitionRestService;

    @Autowired
    private OrganisationRestService organisationRestService;

    @Autowired
    private QuestionRestService questionRestService;
    @Autowired
    private QuestionnaireResponseLinkRestService questionnaireResponseLinkRestService;
    @Autowired
    private QuestionnaireRestService questionnaireRestService;
    @Autowired
    private QuestionnaireResponseRestService questionnaireResponseRestService;
    @Autowired
    private QuestionnaireQuestionRestService questionnaireQuestionRestService;
    @Autowired
    private ProjectFinanceRestService projectFinanceRestService;
    @Autowired
    private QuestionnaireQuestionResponseRestService questionnaireQuestionResponseRestService;
    @Autowired
    private QuestionnaireOptionRestService questionnaireOptionRestService;

    public FinanceChecksFundingRulesViewModel populateFundingRulesViewModel(Long projectId, Long organisationId, boolean editMode) {

        ProjectResource project = projectService.getById(projectId);
        CompetitionResource competition = competitionRestService.getCompetitionById(project.getCompetition()).getSuccess();
        OrganisationResource organisation = organisationRestService.getOrganisationById(organisationId).getSuccess();

        boolean northernIreland = Boolean.TRUE.equals(projectFinanceRestService.getProjectFinance(projectId, organisationId).getSuccess().getNorthernIrelandDeclaration());

        FundingRules fundingRules = northernIreland ? FundingRules.SUBSIDY_CONTROL : FundingRules.STATE_AID;

        OrganisationResource leadOrganisation = projectService.getLeadOrganisation(projectId);

        boolean leadPartnerOrganisation = leadOrganisation.getId().equals(organisation.getId());

        List<QuestionnaireQuestionAnswerViewModel> questionsAndAnswers = questionsAndAnswers(project, organisationId);

        return new FinanceChecksFundingRulesViewModel(project,
                competition,
                organisation,
                leadPartnerOrganisation,
                fundingRules,
                questionsAndAnswers,
                false,
                false, editMode);
    }

    private List<QuestionnaireQuestionAnswerViewModel> questionsAndAnswers(ProjectResource project, Long organisationId) {

        List<QuestionnaireQuestionResource> questions;
        String responseId;
        try {
            QuestionResource subsidyBasisQuestion = questionRestService.getQuestionByCompetitionIdAndQuestionSetupType(project.getCompetition(), QuestionSetupType.SUBSIDY_BASIS).getSuccess();

            if (subsidyBasisQuestion.getQuestionnaireId() == null) {
                // questionnaire does not exist
                return new ArrayList<>();
            }

            StringResource response = questionnaireResponseLinkRestService.getResponseIdByApplicationIdAndOrganisationIdAndQuestionnaireId(subsidyBasisQuestion.getQuestionnaireId(), project.getApplication(), organisationId).getSuccess();
            responseId = response.getContent();
            QuestionnaireResource questionnaire = questionnaireRestService.get(subsidyBasisQuestion.getQuestionnaireId()).getSuccess();
            questions = questionnaireQuestionRestService.get(questionnaire.getQuestions()).getSuccess();

        } catch (ObjectNotFoundException e) {
            // questionnaire does not exist
            return new ArrayList<>();
        }

        List<QuestionnaireQuestionResponseResource> responses;

        try {
            QuestionnaireResponseResource questionnaireResponse = questionnaireResponseRestService.get(responseId).getSuccess();
            responses = questionnaireQuestionResponseRestService.get(questionnaireResponse.getQuestionnaireQuestionResponse()).getSuccess();
        } catch (ObjectNotFoundException e) {
            // questionnaire answer does not exist, make just the questions
            return questions.stream()
                    .map(q -> new QuestionnaireQuestionAnswerViewModel(q.getQuestion(), null))
                    .collect(Collectors.toList());
        }

        // make with questions and answers
        return questions.stream()
                .map(q -> {
                    Optional<QuestionnaireQuestionResponseResource> responseToQuestion = responses.stream()
                            .filter(resp -> q.getId().equals(resp.getQuestion())).findAny();

                    if (responseToQuestion.isPresent()) {
                        Long option = responseToQuestion.get().getOption();
                        QuestionnaireOptionResource optionResource = questionnaireOptionRestService.get(option).getSuccess();
                        return new QuestionnaireQuestionAnswerViewModel(q.getQuestion(), optionResource.getText());
                    } else {
                        return new QuestionnaireQuestionAnswerViewModel(q.getQuestion(), null);
                    }
                })
                .collect(Collectors.toList());
    }
}
