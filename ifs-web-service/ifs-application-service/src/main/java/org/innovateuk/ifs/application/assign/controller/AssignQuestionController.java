package org.innovateuk.ifs.application.assign.controller;

import org.innovateuk.ifs.application.assign.form.AssignQuestionForm;
import org.innovateuk.ifs.application.assign.populator.AssignQuestionModelPopulator;
import org.innovateuk.ifs.application.resource.QuestionStatusResource;
import org.innovateuk.ifs.application.service.QuestionService;
import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.controller.ValidationHandler;
import org.innovateuk.ifs.origin.AssignQuestionOrigin;
import org.innovateuk.ifs.user.resource.ProcessRoleResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.service.UserRestService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

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

    private UserRestService userRestService;

    private QuestionService questionService;

    private AssignQuestionModelPopulator assignQuestionModelPopulator;

    public AssignQuestionController(
            UserRestService userRestService,
            QuestionService questionService,
            AssignQuestionModelPopulator assignQuestionModelPopulator
    ) {
        this.userRestService = userRestService;
        this.questionService = questionService;
        this.assignQuestionModelPopulator = assignQuestionModelPopulator;
    }

    @GetMapping("/question/{questionId}/assign")
    public String getAssignPage(@ModelAttribute(name = "form", binding = false) AssignQuestionForm form,
                                @PathVariable("questionId") long questionId,
                                @PathVariable("applicationId") long applicationId,
                                @RequestParam(value = "origin", defaultValue = "OVERVIEW") String origin,
                                Model model) {
        populateAssigneeForm(questionId, applicationId, form);
        return doViewAssignPage(model, questionId, applicationId, origin);
    }

    @PostMapping("/question/{questionId}/assign")
    public String assign(@Valid @ModelAttribute("form") AssignQuestionForm form,
                         @SuppressWarnings("unused") BindingResult bindingResult,
                         ValidationHandler validationHandler,
                         @PathVariable("questionId") long questionId,
                         @PathVariable ("applicationId") long applicationId,
                         @RequestParam(value = "origin", defaultValue = "OVERVIEW") String origin,
                         Model model,
                         UserResource loggedInUser) {
        Supplier<String> failureView = () -> doViewAssignPage(model, questionId, applicationId, origin);
        ProcessRoleResource assignedBy = userRestService.findProcessRole(loggedInUser.getId(), applicationId).getSuccess();
        return validationHandler.failNowOrSucceedWith(failureView, () -> {
            ServiceResult<Void> assignResult = questionService.assign(questionId, applicationId, form.getAssignee(), assignedBy.getId());

            return validationHandler.addAnyErrors(assignResult)
                    .failNowOrSucceedWith(failureView, () -> redirectToRelevantPage(applicationId, questionId, origin));
        });
    }

    private String doViewAssignPage(Model model, long questionId, long applicationId, String origin) {

        model.addAttribute("model", assignQuestionModelPopulator.populateModel(questionId, applicationId, origin));
        return "application/questions/assign-question";
    }

    private String redirectToRelevantPage(long applicationId, long questionId, String origin) {
        AssignQuestionOrigin questionOrigin  = AssignQuestionOrigin.valueOf(origin);
        return "redirect:" + questionOrigin.getOriginUrl();
    }

    private void populateAssigneeForm(long questionId, long applicationId, AssignQuestionForm form) {
        List<QuestionStatusResource> statuses = questionService.findQuestionStatusesByQuestionAndApplicationId(questionId, applicationId);
        if(!statuses.isEmpty()) {
            form.setAssignee(statuses.get(0).getAssignee());
        }
    }
}
