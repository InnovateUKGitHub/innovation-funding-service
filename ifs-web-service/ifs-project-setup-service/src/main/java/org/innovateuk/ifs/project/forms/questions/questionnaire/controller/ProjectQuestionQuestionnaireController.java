package org.innovateuk.ifs.project.forms.questions.questionnaire.controller;


import org.innovateuk.ifs.application.forms.questions.questionnaire.form.QuestionQuestionnaireForm;
import org.innovateuk.ifs.commons.exception.ObjectNotFoundException;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.controller.ValidationHandler;
import org.innovateuk.ifs.finance.resource.ProjectFinanceResource;
import org.innovateuk.ifs.project.finance.service.ProjectFinanceRestService;
import org.innovateuk.ifs.project.forms.questions.questionnaire.populator.ProjectQuestionQuestionnaireModelPopulator;
import org.innovateuk.ifs.project.forms.questions.questionnaire.viewmodel.ProjectQuestionQuestionnaireViewModel;
import org.innovateuk.ifs.project.projectteam.PendingPartnerProgressRestService;
import org.innovateuk.ifs.questionnaire.resource.QuestionnaireDecisionImplementation;
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


@Controller
@RequestMapping("project/{projectId}/form/organisation/{organisationId}/question/{questionId}/questionnaire")
@SecuredBySpring(value = "Controller", description = "Only applicants can edit a question", securedType = ProjectQuestionQuestionnaireController.class)
@PreAuthorize("hasAnyAuthority('applicant')")
public class ProjectQuestionQuestionnaireController {

    @Autowired
    NavigationUtils navigationUtils;

    @Autowired
    private ProjectQuestionQuestionnaireModelPopulator populator;

    @Autowired
    private PendingPartnerProgressRestService pendingPartnerProgressRestService;

    @Autowired
    private ProjectFinanceRestService projectFinanceRestService;

    @SecuredBySpring(value = "VIEW_PROJECT_QUESTION_QUESTIONNAIRE", description = "Applicants and internal users can view the project question questionnaire page")
    @PreAuthorize("hasAnyAuthority('applicant', 'support', 'innovation_lead', 'ifs_administrator', 'comp_admin', 'stakeholder', 'external_finance', 'knowledge_transfer_adviser', 'supporter', 'assessor')")
    @GetMapping
    public String view(@ModelAttribute(name = "form", binding = false) QuestionQuestionnaireForm form,
                       @SuppressWarnings("unused") BindingResult bindingResult,
                       Model model,
                       @PathVariable long projectId,
                       @PathVariable long organisationId,
                       @PathVariable long questionId,
                       UserResource user,
                       HttpServletRequest request) {
        ProjectQuestionQuestionnaireViewModel viewModel = populator.populate(user, projectId, questionId, organisationId);

        if (viewModel.navigateStraightToQuestionnaireWelcome()) {
            return navigationUtils.getRedirectToSameDomainUrl(request, String.format("questionnaire/%s", viewModel.getQuestionnaireResponseId()));
        }
        model.addAttribute("model", viewModel);
        return "project/questions/questionnaire";
    }

    @GetMapping("/questionnaire-complete")
    public String questionnaireComplete(
            @PathVariable long projectId,
            @PathVariable long organisationId,
            @PathVariable long questionId,
            @RequestParam(required = false) QuestionnaireDecisionImplementation outcome) {
        if (outcome != null) {
            switch (outcome) {
                case SET_NORTHERN_IRELAND_DECLARATION_TRUE:
                    setNorthernIrelandDeclaration(projectId, organisationId, true);
                    break;
                case SET_NORTHERN_IRELAND_DECLARATION_FALSE:
                    setNorthernIrelandDeclaration(projectId, organisationId,false);
                    break;
                default:
                    throw new ObjectNotFoundException("Unknown outcome type " + outcome);
            }
        }
        return "redirect:" + viewRedirectUrl(projectId, organisationId, questionId);
    }

    @PostMapping
    public String save(@PathVariable long projectId, @PathVariable long organisationId) {
        return redirectToOverview(projectId, organisationId);
    }

    @PostMapping(params = "complete")
    public String complete(Model model,
                           UserResource user,
                           @PathVariable long projectId,
                           @PathVariable long organisationId,
                           @PathVariable long questionId,
                           @Valid @ModelAttribute("form") QuestionQuestionnaireForm form,
                           BindingResult bindingResult,
                           ValidationHandler validationHandler,
                           HttpServletRequest request) {
        Supplier<String> successView = () -> redirectToOverview(projectId, organisationId);
        Supplier<String> failureView = () -> view(form, bindingResult, model, projectId, organisationId, questionId, user, request);
        return validationHandler.failNowOrSucceedWith(failureView, () -> {
            RestResult<Void> result = pendingPartnerProgressRestService.markSubsidyBasisComplete(projectId, organisationId);
            validationHandler.addAnyErrors(result);
            return validationHandler.failNowOrSucceedWith(failureView, successView);
        });
    }

    @PostMapping(params = "edit")
    public String edit(UserResource user,
                       @PathVariable long projectId,
                       @PathVariable long organisationId,
                       @PathVariable long questionId) {
        pendingPartnerProgressRestService.markSubsidyBasisIncomplete(projectId, organisationId);
        return "redirect:" + viewRedirectUrl(projectId, organisationId, questionId);
    }

    private void setNorthernIrelandDeclaration(long projectId, long organisationId, boolean value) {
        ProjectFinanceResource finance = projectFinanceRestService.getProjectFinance(projectId, organisationId).getSuccess();
        finance.setNorthernIrelandDeclaration(value);
        projectFinanceRestService.update(finance).getSuccess();
    }

    private String viewRedirectUrl(long projectId, long organisationId, long questionId) {
        return String.format("/project/%d/form/organisation/%d/question/%d/questionnaire", projectId, organisationId, questionId);
    }

    private String redirectToOverview(long projectId, long organisationId) {
        return String.format("redirect:/project/%d/organisation/%d/pending-partner-progress", projectId, organisationId);
    }
}
