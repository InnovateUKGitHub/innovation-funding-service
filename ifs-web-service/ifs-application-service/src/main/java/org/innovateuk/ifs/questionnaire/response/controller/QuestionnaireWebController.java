package org.innovateuk.ifs.questionnaire.response.controller;


import org.innovateuk.ifs.commons.exception.IFSRuntimeException;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.controller.ValidationHandler;
import org.innovateuk.ifs.questionnaire.config.service.QuestionnaireOptionRestService;
import org.innovateuk.ifs.questionnaire.config.service.QuestionnaireQuestionRestService;
import org.innovateuk.ifs.questionnaire.config.service.QuestionnaireRestService;
import org.innovateuk.ifs.questionnaire.config.service.QuestionnaireTextOutcomeRestService;
import org.innovateuk.ifs.questionnaire.link.service.QuestionnaireResponseLinkRestService;
import org.innovateuk.ifs.questionnaire.resource.*;
import org.innovateuk.ifs.questionnaire.response.form.QuestionnaireQuestionForm;
import org.innovateuk.ifs.questionnaire.response.populator.QuestionnaireQuestionViewModelPopulator;
import org.innovateuk.ifs.questionnaire.response.service.QuestionnaireQuestionResponseRestService;
import org.innovateuk.ifs.questionnaire.response.service.QuestionnaireResponseRestService;
import org.innovateuk.ifs.questionnaire.response.viewmodel.QuestionnaireWelcomeViewModel;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.util.NavigationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.function.Supplier;

import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;

@Controller
@RequestMapping("/questionnaire")
@PreAuthorize("permitAll")
@SecuredBySpring(value = "Controller", description = "Anyone can access the questionnaire pages. The detailed security checks are done by the data service.", securedType = QuestionnaireWebController.class)
public class QuestionnaireWebController {

    protected static final String REDIRECT_URL_COOKIE_KEY = "QUESTIONNAIRE_REDIRECT_URL";

    @Autowired
    private QuestionnaireRestService questionnaireRestService;

    @Autowired
    private QuestionnaireQuestionRestService questionnaireQuestionRestService;

    @Autowired
    private QuestionnaireOptionRestService questionnaireOptionRestService;

    @Autowired
    private QuestionnaireResponseRestService questionnaireResponseRestService;

    @Autowired
    private QuestionnaireQuestionResponseRestService questionnaireQuestionResponseRestService;

    @Autowired
    private QuestionnaireTextOutcomeRestService questionnaireTextOutcomeRestService;

    @Autowired
    private QuestionnaireQuestionViewModelPopulator questionnaireQuestionViewModelPopulator;

    @Autowired
    private QuestionnaireResponseLinkRestService linkRestService;

    @Autowired
    private NavigationUtils navigationUtils;

    @GetMapping("/{questionnaireResponseId}")
    public String welcomeScreen(Model model,
                                HttpServletRequest request,
                                UserResource user,
                                @PathVariable String questionnaireResponseId
    ) {
        QuestionnaireResponseResource response = questionnaireResponseRestService.get(questionnaireResponseId).getSuccess();
        QuestionnaireResource questionnaire = questionnaireRestService.get(response.getQuestionnaire()).getSuccess();
        QuestionnaireWelcomeViewModel viewModel = new QuestionnaireWelcomeViewModel(questionnaire);
        if (questionnaire.getSecurityType() == QuestionnaireSecurityType.LINK) {
            QuestionnaireLinkResource link = linkRestService.getQuestionnaireLink(questionnaireResponseId).getSuccess();
            if (link instanceof ApplicationOrganisationLinkResource) {
                ApplicationOrganisationLinkResource applicationOrganisationLink = (ApplicationOrganisationLinkResource) link;
                viewModel = new QuestionnaireWelcomeViewModel(
                        questionnaire,
                        applicationOrganisationLink.getApplicationName(),
                        String.format("~/application/%d", applicationOrganisationLink.getApplicationId()),
                        "Return to application overview");
            }
            else if (link instanceof ProjectOrganisationLinkResource) {
                ProjectOrganisationLinkResource projectOrganisationLinkResource = (ProjectOrganisationLinkResource) link;
                viewModel = new QuestionnaireWelcomeViewModel(
                        questionnaire,
                        projectOrganisationLinkResource.getProjectName(),
                        String.format("~/project-setup/project/%d/organisation/%d/pending-partner-progress", projectOrganisationLinkResource.getProjectId(), projectOrganisationLinkResource.getOrganisationId()),
                        "Return to join project");
            }
        }
        model.addAttribute("model", viewModel);
        return "questionnaire/welcome";
    }


    @PostMapping("/{questionnaireResponseId}")
    public String start(Model model,
                        HttpServletRequest request,
                        UserResource user,
                        @PathVariable String questionnaireResponseId) {
        QuestionnaireResponseResource response = questionnaireResponseRestService.get(questionnaireResponseId).getSuccess();
        QuestionnaireResource questionnaire = questionnaireRestService.get(response.getQuestionnaire()).getSuccess();
        return String.format("redirect:/questionnaire/%s/question/%d", questionnaireResponseId, questionnaire.getQuestions().get(0));
    }

    @GetMapping("/{questionnaireResponseId}/question/{questionId}")
    public String question(Model model,
                           HttpServletRequest request,
                           UserResource user,
                           @PathVariable String questionnaireResponseId,
                           @PathVariable long questionId) {
        QuestionnaireQuestionForm form = new QuestionnaireQuestionForm();
        questionnaireQuestionResponseRestService.findByQuestionnaireQuestionIdAndQuestionnaireResponseId(questionId, questionnaireResponseId)
                .toOptionalIfNotFound()
                .getSuccess()
                .ifPresent(questionResponse -> {
                    form.setQuestionResponseId(questionResponse.getId());
                    form.setOption(questionResponse.getOption());
                });
        model.addAttribute("form", form);
        return viewQuestion(model, questionnaireResponseId, questionId);
    }

    @PostMapping("/{questionnaireResponseId}/question/{questionId}")
    public String saveQuestionResponse(@Valid @ModelAttribute("form") QuestionnaireQuestionForm form,
                                       BindingResult result,
                                       ValidationHandler validationHandler,
                                       Model model,
                                       HttpServletRequest request,
                                       UserResource user,
                                       @PathVariable String questionnaireResponseId,
                                       @PathVariable long questionId) {
        Supplier<String> successView = () -> {
            QuestionnaireOptionResource option = questionnaireOptionRestService.get(form.getOption()).getSuccess();
            if (option.getDecisionType() == DecisionType.QUESTION) {
                return String.format("redirect:/questionnaire/%s/question/%d", questionnaireResponseId, option.getDecision());
            } else if (option.getDecisionType() == DecisionType.TEXT_OUTCOME) {
                return String.format("redirect:/questionnaire/%s/outcome/%d", questionnaireResponseId, option.getDecision());
            }
            throw new IFSRuntimeException("Unknown decision type " + option.getDecisionType());
        };
        Supplier<String> failureView = () -> viewQuestion(model, questionnaireResponseId, questionId);


        return validationHandler.failNowOrSucceedWith(failureView, () -> {
            RestResult<Void> saveResult;
            if (form.getQuestionResponseId() != null) {
                QuestionnaireQuestionResponseResource response = questionnaireQuestionResponseRestService.get(form.getQuestionResponseId()).getSuccess();
                response.setOption(form.getOption());
                saveResult = questionnaireQuestionResponseRestService.update(response.getId(), response);
            } else {
                QuestionnaireQuestionResponseResource response = new QuestionnaireQuestionResponseResource();
                response.setQuestionnaireResponse(questionnaireResponseId);
                response.setOption(form.getOption());
                saveResult = questionnaireQuestionResponseRestService.create(response).andOnSuccess(() -> restSuccess());
            }
            validationHandler.addAnyErrors(saveResult);
            return validationHandler.failNowOrSucceedWith(failureView, successView);
        });
    }

    @GetMapping("/{questionnaireResponseId}/outcome/{outcomeId}")
    public String outcome(Model model,
                           HttpServletRequest request,
                           UserResource user,
                           @PathVariable String questionnaireResponseId,
                           @PathVariable long outcomeId) {
        QuestionnaireResponseResource response = questionnaireResponseRestService.get(questionnaireResponseId).getSuccess();
        QuestionnaireResource questionnaire = questionnaireRestService.get(response.getQuestionnaire()).getSuccess();
        QuestionnaireTextOutcomeResource outcome = questionnaireTextOutcomeRestService.get(outcomeId).getSuccess();
        String endUrl = getEndUrl(questionnaire, questionnaireResponseId, request);
        if (outcome.getImplementation() != null) {
            endUrl += "?outcome=" + outcome.getImplementation().name();
        }
        if (outcome.getText() == null) {
            return "redirect:" + endUrl;
        }
        model.addAttribute("model", outcome);
        model.addAttribute("endUrl", endUrl);
        return "questionnaire/outcome";
    }

    private String getEndUrl(QuestionnaireResource questionnaire, String questionnaireResponseId, HttpServletRequest request) {
        if (questionnaire.getSecurityType() == QuestionnaireSecurityType.LINK) {
            QuestionnaireLinkResource link = linkRestService.getQuestionnaireLink(questionnaireResponseId).getSuccess();
            if (link instanceof ApplicationOrganisationLinkResource) {
                return String.format("/application/%d/form/organisation/%d/question/%d/questionnaire/questionnaire-complete", ((ApplicationOrganisationLinkResource) link).getApplicationId(), ((ApplicationOrganisationLinkResource) link).getOrganisationId(), ((ApplicationOrganisationLinkResource) link).getQuestionId());
            }
            if (link instanceof ProjectOrganisationLinkResource) {
                String redirectUrl = String.format("project-setup/project/%d/form/organisation/%d/question/%d/questionnaire/questionnaire-complete", ((ProjectOrganisationLinkResource) link).getProjectId(), ((ProjectOrganisationLinkResource) link).getOrganisationId(), ((ProjectOrganisationLinkResource) link).getQuestionId());
                return navigationUtils.getDirectToSameDomainUrl(request, redirectUrl);
            }
        }
        return "/";
    }

    private String viewQuestion(Model model, String questionnaireResponseId, long questionId) {
        model.addAttribute("model", questionnaireQuestionViewModelPopulator.populate(questionnaireResponseId, questionId));
        return "questionnaire/question";
    }
}
