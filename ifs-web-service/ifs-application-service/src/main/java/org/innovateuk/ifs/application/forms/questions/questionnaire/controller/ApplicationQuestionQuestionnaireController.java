package org.innovateuk.ifs.application.forms.questions.questionnaire.controller;


import org.innovateuk.ifs.application.forms.questions.questionnaire.form.QuestionQuestionnaireForm;
import org.innovateuk.ifs.application.forms.questions.questionnaire.populator.ApplicationQuestionQuestionnaireModelPopulator;
import org.innovateuk.ifs.application.forms.questions.questionnaire.viewmodel.ApplicationQuestionQuestionnaireViewModel;
import org.innovateuk.ifs.application.service.QuestionStatusRestService;
import org.innovateuk.ifs.commons.error.ValidationMessages;
import org.innovateuk.ifs.commons.exception.ObjectNotFoundException;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.controller.ValidationHandler;
import org.innovateuk.ifs.finance.resource.ApplicationFinanceResource;
import org.innovateuk.ifs.finance.service.ApplicationFinanceRestService;
import org.innovateuk.ifs.questionnaire.resource.QuestionnaireDecisionImplementation;
import org.innovateuk.ifs.user.resource.ProcessRoleResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.service.ProcessRoleRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.function.Supplier;

import static org.innovateuk.ifs.application.forms.ApplicationFormUtil.APPLICATION_BASE_URL;

@Controller
@RequestMapping(APPLICATION_BASE_URL + "{applicationId}/form/organisation/{organisationId}/question/{questionId}/questionnaire")
@SecuredBySpring(value = "Controller", description = "Only applicants can edit a question", securedType = ApplicationQuestionQuestionnaireController.class)
@PreAuthorize("hasAnyAuthority('applicant')")
public class ApplicationQuestionQuestionnaireController {

    @Autowired
    private ApplicationQuestionQuestionnaireModelPopulator populator;

    @Autowired
    private ApplicationFinanceRestService applicationFinanceRestService;

    @Autowired
    private QuestionStatusRestService questionStatusRestService;

    @Autowired
    private ProcessRoleRestService processRoleRestService;


    @SecuredBySpring(value = "VIEW_APPLICATION_QUESTION_QUESTIONNAIRE", description = "Applicants and internal users can view the application question questionnaire page")
    @PreAuthorize("hasAnyAuthority('applicant', 'support', 'innovation_lead', 'ifs_administrator', 'comp_admin', 'stakeholder', 'external_finance', 'knowledge_transfer_adviser', 'supporter', 'assessor', 'monitoring_officer')")
    @GetMapping
    public String view(@ModelAttribute(name = "form", binding = false) QuestionQuestionnaireForm form,
                       @SuppressWarnings("unused") BindingResult bindingResult,
                       Model model,
                       @PathVariable long applicationId,
                       @PathVariable long organisationId,
                       @PathVariable long questionId,
                       UserResource user) {
        ApplicationQuestionQuestionnaireViewModel viewModel = populator.populate(user, applicationId, questionId, organisationId);

        if (viewModel.navigateStraightToQuestionnaireWelcome()) {
            return String.format("redirect:/questionnaire/%s", viewModel.getQuestionnaireResponseId());
        }
        model.addAttribute("model", viewModel);
        return "application/questions/questionnaire";
    }

    @GetMapping("/questionnaire-complete")
    public String questionnaireComplete(
            @PathVariable long applicationId,
            @PathVariable long organisationId,
            @PathVariable long questionId,
            @RequestParam(required = false) QuestionnaireDecisionImplementation outcome) {
        if (outcome != null) {
            switch (outcome) {
                case SET_NORTHERN_IRELAND_DECLARATION_TRUE:
                    setNorthernIrelandDeclaration(applicationId, organisationId, true);
                    break;
                case SET_NORTHERN_IRELAND_DECLARATION_FALSE:
                    setNorthernIrelandDeclaration(applicationId, organisationId,false);
                    break;
                default:
                    throw new ObjectNotFoundException("Unkown outcome type " + outcome);
            }
        }
        return "redirect:" + viewRedirectUrl(applicationId, organisationId, questionId);
    }

    @PostMapping
    public String save(@PathVariable long applicationId) {
        return redirectToOverview(applicationId);
    }

    @PostMapping(params = "complete")
    public String complete(Model model,
                           UserResource user,
                           @PathVariable long applicationId,
                           @PathVariable long organisationId,
                           @PathVariable long questionId,
                           @Valid @ModelAttribute("form") QuestionQuestionnaireForm form,
                           BindingResult bindingResult,
                           ValidationHandler validationHandler) {
        Supplier<String> successView = () -> redirectToOverview(applicationId);
        Supplier<String> failureView = () -> view(form, bindingResult, model, applicationId, organisationId, questionId, user);
        return validationHandler.failNowOrSucceedWith(failureView, () -> {
            ProcessRoleResource processRole = processRoleRestService.findProcessRole(user.getId(), applicationId).getSuccess();
            RestResult<List<ValidationMessages>> result = questionStatusRestService.markAsComplete(questionId, applicationId, processRole.getId());
                validationHandler.addAnyErrors(result);
                return validationHandler.failNowOrSucceedWith(failureView, successView);
        });
    }

    @PostMapping(params = "edit")
    public String edit(UserResource user,
                       @PathVariable long applicationId,
                       @PathVariable long organisationId,
                       @PathVariable long questionId) {
        ProcessRoleResource processRole = processRoleRestService.findProcessRole(user.getId(), applicationId).getSuccess();
        questionStatusRestService.markAsInComplete(questionId, applicationId, processRole.getId()).getSuccess();
        return "redirect:" + viewRedirectUrl(applicationId, organisationId, questionId);
    }

    private void setNorthernIrelandDeclaration(long applicationId, long organisationId, boolean value) {
        ApplicationFinanceResource finance = applicationFinanceRestService.getFinanceDetails(applicationId, organisationId).getSuccess();
        finance.setNorthernIrelandDeclaration(value);
        applicationFinanceRestService.update(finance.getId(), finance).getSuccess();
    }

    private String viewRedirectUrl(long applicationId, long organisationId, long questionId) {
        return String.format("/application/%d/form/organisation/%d/question/%d/questionnaire", applicationId, organisationId, questionId);
    }

    private String redirectToOverview(long applicationId) {
        return String.format("redirect:/application/%d/", applicationId);
    }
}
