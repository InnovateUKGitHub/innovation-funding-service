package org.innovateuk.ifs.project.milestones.controller;

import org.innovateuk.ifs.application.ProcurementMilestones.AbstractProcurementMilestoneController;
import org.innovateuk.ifs.application.forms.sections.procurement.milestones.form.ProcurementMilestonesForm;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.controller.ValidationHandler;
import org.innovateuk.ifs.procurement.milestone.service.ProjectProcurementMilestoneRestService;
import org.innovateuk.ifs.project.finance.service.FinanceCheckRestService;
import org.innovateuk.ifs.project.milestones.form.ProjectProcurementMilestoneApprovalForm;
import org.innovateuk.ifs.project.milestones.saver.ProjectProcurementMilestoneFormSaver;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.function.Supplier;

@Controller
@RequestMapping("/project/{projectId}/finance-check/organisation/{organisationId}/procurement-milestones")
public class ProjectProcurementMilestonesController extends AbstractProcurementMilestoneController {

    private static final String VIEW = "milestones/project-procurement-milestones";

    @Autowired
    private ProjectProcurementMilestoneRestService restService;

    @Autowired
    private FinanceCheckRestService financeCheckRestService;

    @Autowired
    private ProjectProcurementMilestoneFormSaver saver;

    @GetMapping
    @PreAuthorize("hasPermission(#projectId, 'org.innovateuk.ifs.project.resource.ProjectCompositeId', 'ACCESS_FINANCE_CHECKS_SECTION')")
    public String viewMilestones(@PathVariable long projectId,
                                 @PathVariable long organisationId,
                                 @RequestParam(name = "editMilestones", defaultValue = "false") boolean editMilestones,
                                 UserResource userResource,
                                 Model model) {
        model.addAttribute("projectProcurementMilestoneApprovalForm", new ProjectProcurementMilestoneApprovalForm());
        return viewProjectSetupMilestones(model, projectId, organisationId, userResource, editMilestones);
    }

    @PreAuthorize("hasPermission(#projectId, 'org.innovateuk.ifs.project.resource.ProjectCompositeId', 'ACCESS_FINANCE_CHECKS_SECTION')")
    @PostMapping(params = "approve")
    public String approvePaymentMilestones(@PathVariable long projectId,
                                           @PathVariable long organisationId,
                                           @ModelAttribute("form") ProjectProcurementMilestoneApprovalForm form,
                                           @SuppressWarnings("unused") BindingResult bindingResult,
                                           ValidationHandler validationHandler,
                                           Model model,
                                           UserResource user) {
        Supplier<String> view = () -> viewMilestones(projectId, organisationId, false, user, model);
        RestResult<Void> approvePaymentMilestoneState = financeCheckRestService.approvePaymentMilestoneState(projectId, organisationId);
        return validationHandler
                .addAnyErrors(approvePaymentMilestoneState)
                .failNowOrSucceedWith(view, view);
    }

    @PreAuthorize("hasPermission(#projectId, 'org.innovateuk.ifs.project.resource.ProjectCompositeId', 'ACCESS_FINANCE_CHECKS_SECTION')")
    @PostMapping(params = "save")
    public String saveMilestones(@PathVariable long projectId,
                                 @PathVariable long organisationId,
                                 @ModelAttribute("form") ProcurementMilestonesForm form,
                                 @SuppressWarnings("unused") BindingResult bindingResult,
                                 ValidationHandler validationHandler,
                                 Model model,
                                 UserResource user) {
        Supplier<String> failureView = () -> viewMilestones(projectId, organisationId, true, user, model);
        Supplier<String> successView = redirectToViewMilestones(projectId, organisationId);

        return validationHandler.failNowOrSucceedWith(failureView, () -> {
            validationHandler.addAnyErrors(saver.save(form, projectId, organisationId));
            return validationHandler.failNowOrSucceedWith(failureView, () -> {
                validationHandler.addAnyErrors(
                        financeCheckRestService.resetPaymentMilestoneState(projectId, organisationId));
                        return validationHandler.failNowOrSucceedWith(failureView, successView);
            });
        });
    }

    private Supplier<String> redirectToViewMilestones(long projectId, long organisationId) {
        return () -> String.format("redirect:/project/%d/finance-check/organisation/%d/procurement-milestones", projectId, organisationId);
    }

    @Override
    protected String getView() {
        return VIEW;
    }
}
