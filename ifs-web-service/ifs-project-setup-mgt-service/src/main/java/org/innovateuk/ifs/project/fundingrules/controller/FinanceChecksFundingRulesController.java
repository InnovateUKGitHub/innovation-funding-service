package org.innovateuk.ifs.project.fundingrules.controller;

import org.innovateuk.ifs.application.service.QuestionRestService;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.FundingRules;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.controller.ValidationHandler;
import org.innovateuk.ifs.form.resource.QuestionResource;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.project.ProjectService;
import org.innovateuk.ifs.project.finance.service.FinanceCheckRestService;
import org.innovateuk.ifs.project.finance.service.ProjectFinanceRestService;
import org.innovateuk.ifs.project.fundingrules.form.FinanceChecksFundingRulesForm;
import org.innovateuk.ifs.project.fundingrules.viewmodel.FinanceChecksFundingRulesViewModel;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.question.resource.QuestionSetupType;
import org.innovateuk.ifs.questionnaire.config.service.QuestionnaireOptionRestService;
import org.innovateuk.ifs.questionnaire.config.service.QuestionnaireQuestionRestService;
import org.innovateuk.ifs.questionnaire.config.service.QuestionnaireRestService;
import org.innovateuk.ifs.questionnaire.config.service.QuestionnaireTextOutcomeRestService;
import org.innovateuk.ifs.questionnaire.link.service.QuestionnaireResponseLinkRestService;
import org.innovateuk.ifs.questionnaire.resource.*;
import org.innovateuk.ifs.questionnaire.response.service.QuestionnaireQuestionResponseRestService;
import org.innovateuk.ifs.questionnaire.response.service.QuestionnaireResponseRestService;
import org.innovateuk.ifs.string.resource.StringResource;
import org.innovateuk.ifs.user.service.OrganisationRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.function.Supplier;

/**
 * This controller serves the Funding Rules page where internal users can confirm the funding rules of a partner organisation's
 * financial position on a Project
 */
@Controller
@PreAuthorize("hasAnyAuthority('project_finance', 'comp_admin', 'external_finance')")
@SecuredBySpring(value = "Controller", description = "TODO", securedType = FinanceChecksFundingRulesController.class)
@RequestMapping("/project/{projectId}/finance-check/organisation/{organisationId}/funding-rules")
public class FinanceChecksFundingRulesController {

    @Autowired
    private ProjectService projectService;

    @Autowired
    private OrganisationRestService organisationRestService;

    @Autowired
    private CompetitionRestService competitionRestService;

    @Autowired
    private FinanceCheckRestService financeCheckRestService;

    @Autowired
    private ProjectFinanceRestService projectFinanceRestService;

    @Autowired
    private QuestionRestService questionRestService;

    @Autowired
    private QuestionnaireRestService questionnaireRestService;

    @Autowired
    private QuestionnaireResponseRestService questionnaireResponseRestService;

    @Autowired
    private QuestionnaireQuestionRestService questionnaireQuestionRestService;

    @Autowired
    private QuestionnaireQuestionResponseRestService questionnaireQuestionResponseRestService;

    @Autowired
    private QuestionnaireResponseLinkRestService questionnaireResponseLinkRestService;

    @Autowired
    private QuestionnaireOptionRestService questionnaireOptionRestService;

    @Autowired
    private QuestionnaireTextOutcomeRestService questionnaireTextOutcomeRestService;

    @GetMapping
    public String viewFundingRules(@PathVariable("projectId") Long projectId,
                                   @PathVariable("organisationId") Long organisationId, Model model) {

        return doViewFundingRules(projectId, organisationId, model, getFundingRulesForm(projectId, organisationId));
    }

    @PostMapping(params = "save-and-continue")
    public String saveAndContinue(@PathVariable("projectId") Long projectId,
                                  @PathVariable("organisationId") Long organisationId,
                                  @ModelAttribute("form") FinanceChecksFundingRulesForm form,
                                  @SuppressWarnings("unused") BindingResult bindingResult,
                                  ValidationHandler validationHandler,
                                  Model model) {

        Supplier<String> successView = () -> "redirect:/project/" + projectId + "/finance-check";

        return doSaveFundingRules(projectId, organisationId, form, validationHandler, model, successView);
    }

    @PostMapping(params = "approve-funding-rules")
    public String approveFundingRules(@PathVariable("projectId") Long projectId,
                                      @PathVariable("organisationId") Long organisationId,
                                      @ModelAttribute("form") FinanceChecksFundingRulesForm form,
                                      @SuppressWarnings("unused") BindingResult bindingResult,
                                      ValidationHandler validationHandler,
                                      Model model) {

        Supplier<String> successView = () ->
                "redirect:/project/" + projectId + "/finance-check/organisation/" + organisationId + "/funding-rules";

        return doSaveFundingRules(projectId, organisationId, form, validationHandler, model, successView);
    }

    private String doSaveFundingRules(Long projectId, Long organisationId, FinanceChecksFundingRulesForm form,
                                      ValidationHandler validationHandler, Model model, Supplier<String> successView) {

        Supplier<String> failureView = () -> doViewFundingRules(projectId, organisationId, model, form);

        return validationHandler.
                failNowOrSucceedWith(failureView, () -> {

                    FundingRules fundingRules = form.isChangeToStateAid() ? FundingRules.STATE_AID : FundingRules.SUBSIDY_CONTROL;

                    RestResult<Void> saveFundingRulesResult = financeCheckRestService.saveFundingRules(projectId, organisationId, fundingRules);

                    return validationHandler.
                    addAnyErrors(saveFundingRulesResult).
                            failNowOrSucceedWith(failureView, successView);
                });
    }

    private String doViewFundingRules(Long projectId, Long organisationId, Model model, FinanceChecksFundingRulesForm form) {
        model.addAttribute("model", getViewModel(projectId, organisationId));
        model.addAttribute("form", form);

        return "project/financecheck/fundingrules";
    }

    private FinanceChecksFundingRulesViewModel getViewModel(Long projectId, Long organisationId) {

        ProjectResource project = projectService.getById(projectId);
        CompetitionResource competition = competitionRestService.getCompetitionById(project.getCompetition()).getSuccess();
        OrganisationResource organisation = organisationRestService.getOrganisationById(organisationId).getSuccess();


        QuestionResource subsidyBasisQuestion = questionRestService.getQuestionByCompetitionIdAndQuestionSetupType(project.getCompetition(), QuestionSetupType.SUBSIDY_BASIS).getSuccess();
        StringResource responseId = questionnaireResponseLinkRestService.getResponseIdByApplicationIdAndOrganisationIdAndQuestionnaireId( subsidyBasisQuestion.getQuestionnaireId(), project.getApplication(), organisationId).getSuccess();
        QuestionnaireResource questionnaire = questionnaireRestService.get(subsidyBasisQuestion.getQuestionnaireId()).getSuccess();
        QuestionnaireResponseResource questionnaireResponse = questionnaireResponseRestService.get(responseId.getContent()).getSuccess();
        List<QuestionnaireQuestionResource> questions = questionnaireQuestionRestService.get(questionnaire.getQuestions()).getSuccess();
        List<QuestionnaireQuestionResponseResource> responses = questionnaireQuestionResponseRestService.get(questionnaireResponse.getQuestionnaireQuestionResponse()).getSuccess();

        // questionnaire-determined outcome will be the text outcome of the last question.
        QuestionnaireQuestionResource lastQuestion = questions.get(questions.size() - 1);
        QuestionnaireQuestionResponseResource lastAnswer = responses.get(responses.size() - 1);
        QuestionnaireOptionResource option = questionnaireOptionRestService.get(lastAnswer.getOption()).getSuccess();
        QuestionnaireTextOutcomeResource textOutcome = questionnaireTextOutcomeRestService.get(option.getDecision()).getSuccess();

        boolean northernIreland = Boolean.TRUE.equals(projectFinanceRestService.getProjectFinance(projectId, organisationId).getSuccess().getNorthernIrelandDeclaration());

        FundingRules fundingRules = northernIreland ? FundingRules.SUBSIDY_CONTROL : FundingRules.STATE_AID;

        OrganisationResource leadOrganisation = projectService.getLeadOrganisation(projectId);

        boolean leadPartnerOrganisation = leadOrganisation.getId().equals(organisation.getId());

        return new FinanceChecksFundingRulesViewModel(project,
                competition,
                organisation,
                leadPartnerOrganisation,
                fundingRules,
                false);
    }

    private FinanceChecksFundingRulesForm getFundingRulesForm(Long projectId, Long organisationId) {
        return new FinanceChecksFundingRulesForm(false);
    }

}