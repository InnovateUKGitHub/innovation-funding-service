package org.innovateuk.ifs.application.assign.controller;

import org.innovateuk.ifs.application.assign.form.AssignQuestionForm;
import org.innovateuk.ifs.application.assign.populator.AssignQuestionModelPopulator;
import org.innovateuk.ifs.application.resource.QuestionStatusResource;
import org.innovateuk.ifs.application.service.QuestionService;
import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.controller.ValidationHandler;
import org.innovateuk.ifs.filter.CookieFlashMessageFilter;
import org.innovateuk.ifs.navigation.PageHistory;
import org.innovateuk.ifs.navigation.PageHistoryService;
import org.innovateuk.ifs.user.resource.ProcessRoleResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.service.ProcessRoleRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.List;
import java.util.function.Supplier;

import static org.innovateuk.ifs.application.forms.ApplicationFormUtil.APPLICATION_BASE_URL;

/**
 * This controller will handle all requests to do with assigning application questions
 */
@Controller
@RequestMapping(APPLICATION_BASE_URL + "{applicationId}/form")
@SecuredBySpring(value = "Controller", description = "TODO", securedType = AssignQuestionController.class)
@PreAuthorize("hasAnyAuthority('applicant', 'project_finance', 'ifs_administrator', 'comp_admin', 'support', 'innovation_lead', 'assessor', 'monitoring_officer')")
public class AssignQuestionController {

    @Autowired
    private ProcessRoleRestService processRoleRestService;

    @Autowired
    private QuestionService questionService;

    @Autowired
    private AssignQuestionModelPopulator assignQuestionModelPopulator;

    @Autowired
    private CookieFlashMessageFilter cookieFlashMessageFilter;

    @Autowired
    private PageHistoryService pageHistoryService;

    @GetMapping("/question/{questionId}/assign")
    public String getAssignPage(@ModelAttribute(name = "form", binding = false) AssignQuestionForm form,
                                @PathVariable("questionId") long questionId,
                                @PathVariable("applicationId") long applicationId,
                                Model model) {
        populateAssigneeForm(questionId, applicationId, form);
        return doViewAssignPage(model, questionId, applicationId);
    }

    @PostMapping("/question/{questionId}/assign")
    public String assign(@Valid @ModelAttribute("form") AssignQuestionForm form,
                         @SuppressWarnings("unused") BindingResult bindingResult,
                         ValidationHandler validationHandler,
                         @PathVariable("questionId") long questionId,
                         @PathVariable("applicationId") long applicationId,
                         HttpServletRequest request,
                         HttpServletResponse response,
                         Model model,
                         UserResource loggedInUser) {
        Supplier<String> failureView = () -> doViewAssignPage(model, questionId, applicationId);
        ProcessRoleResource assignedBy = processRoleRestService.findProcessRole(loggedInUser.getId(), applicationId).getSuccess();
        return validationHandler.failNowOrSucceedWith(failureView, () -> {
            ServiceResult<Void> assignResult = questionService.assign(questionId, applicationId, form.getAssignee(), assignedBy.getId());

            return validationHandler.addAnyErrors(assignResult)
                    .failNowOrSucceedWith(failureView, () -> redirectToRelevantPage(applicationId, questionId, request, response));
        });
    }

    private String doViewAssignPage(Model model, long questionId, long applicationId) {

        model.addAttribute("model", assignQuestionModelPopulator.populateModel(questionId, applicationId));
        return "application/questions/assign-question";
    }

    private String redirectToRelevantPage(long applicationId, long questionId, HttpServletRequest request, HttpServletResponse response) {
        cookieFlashMessageFilter.setFlashMessage(response, "assignedQuestion");
        String url = pageHistoryService.getPreviousPage(request)
                .map(PageHistory::buildUrl)
                .orElse(String.format("/application/%d/form/question/%d", applicationId, questionId));
        return "redirect:" + url;
    }

    private void populateAssigneeForm(long questionId, long applicationId, AssignQuestionForm form) {
        List<QuestionStatusResource> statuses = questionService.findQuestionStatusesByQuestionAndApplicationId(questionId, applicationId);
        if (!statuses.isEmpty()) {
            form.setAssignee(statuses.get(0).getAssignee());
        }
    }
}
