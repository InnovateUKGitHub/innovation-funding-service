package org.innovateuk.ifs.questionnaire.controller;


import org.innovateuk.ifs.commons.exception.IFSRuntimeException;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.controller.ValidationHandler;
import org.innovateuk.ifs.questionnaire.config.service.QuestionnaireOptionRestService;
import org.innovateuk.ifs.questionnaire.config.service.QuestionnaireQuestionRestService;
import org.innovateuk.ifs.questionnaire.config.service.QuestionnaireRestService;
import org.innovateuk.ifs.questionnaire.config.service.QuestionnaireTextOutcomeRestService;
import org.innovateuk.ifs.questionnaire.form.QuestionnaireQuestionForm;
import org.innovateuk.ifs.questionnaire.populator.QuestionnaireQuestionViewModelPopulator;
import org.innovateuk.ifs.questionnaire.resource.*;
import org.innovateuk.ifs.questionnaire.response.service.QuestionnaireQuestionResponseRestService;
import org.innovateuk.ifs.questionnaire.response.service.QuestionnaireResponseRestService;
import org.innovateuk.ifs.user.resource.UserResource;
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
public class QuestionnaireWebController {

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

    @GetMapping("/{questionnaireResponseId}")
    public String welcomeScreen(Model model,
                                HttpServletRequest request,
                                UserResource user,
                                @PathVariable long questionnaireResponseId) {
        QuestionnaireResponseResource response = questionnaireResponseRestService.get(questionnaireResponseId).getSuccess();
        QuestionnaireResource questionnaire = questionnaireRestService.get(response.getQuestionnaire()).getSuccess();
        return "questionnaire/welcome";
    }

    @PostMapping("/{questionnaireResponseId}")
    public String start(Model model,
                        HttpServletRequest request,
                        UserResource user,
                        @PathVariable long questionnaireResponseId) {
        QuestionnaireResponseResource response = questionnaireResponseRestService.get(questionnaireResponseId).getSuccess();
        QuestionnaireResource questionnaire = questionnaireRestService.get(response.getQuestionnaire()).getSuccess();
        return String.format("redirect:/questionnaire/%d/question/%d", questionnaireResponseId, questionnaire.getQuestions().get(0));
    }

    @GetMapping("/{questionnaireResponseId}/question/{questionId}")
    public String question(Model model,
                           HttpServletRequest request,
                           UserResource user,
                           @PathVariable long questionnaireResponseId,
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
                                       @PathVariable long questionnaireResponseId,
                                       @PathVariable long questionId) {
        Supplier<String> successView = () -> {
            QuestionnaireOptionResource option = questionnaireOptionRestService.get(form.getOption()).getSuccess();
            if (option.getDecisionType() == DecisionType.QUESTION) {
                return String.format("redirect:/questionnaire/%d/question/%d", questionnaireResponseId, option.getDecision());
            } else if (option.getDecisionType() == DecisionType.TEXT_OUTCOME) {
                return String.format("redirect:/questionnaire/%d/outcome/%d", questionnaireResponseId, option.getDecision());
            }
            throw new IFSRuntimeException("Unknown decision type " + option.getDecisionType());
        };
        Supplier<String> failureView = () -> viewQuestion(model, questionnaireResponseId, questionId);

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
    }

    @GetMapping("/{questionnaireResponseId}/outcome/{outcomeId}")
    public String outcome(Model model,
                           HttpServletRequest request,
                           UserResource user,
                           @PathVariable long questionnaireResponseId,
                           @PathVariable long outcomeId) {
        model.addAttribute("model", questionnaireTextOutcomeRestService.get(outcomeId).getSuccess());
        return "questionnaire/outcome";
    }

    private String viewQuestion(Model model, long questionnaireResponseId, long questionId) {
        model.addAttribute("model", questionnaireQuestionViewModelPopulator.populate(questionnaireResponseId, questionId));
        return "questionnaire/question";
    }
}
