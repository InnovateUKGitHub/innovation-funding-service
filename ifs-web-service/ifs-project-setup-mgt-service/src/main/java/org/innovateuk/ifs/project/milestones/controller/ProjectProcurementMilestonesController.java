package org.innovateuk.ifs.project.milestones.controller;

import org.innovateuk.ifs.application.forms.sections.procurement.milestones.populator.ProcurementMilestoneFormPopulator;
import org.innovateuk.ifs.application.procurement.milestones.AbstractProcurementMilestoneController;
import org.innovateuk.ifs.application.forms.sections.procurement.milestones.form.ProcurementMilestonesForm;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.controller.ValidationHandler;
import org.innovateuk.ifs.procurement.milestone.service.ProjectProcurementMilestoneRestService;
import org.innovateuk.ifs.project.finance.service.FinanceCheckRestService;
import org.innovateuk.ifs.project.milestones.form.ProjectProcurementMilestoneApprovalForm;
import org.innovateuk.ifs.project.milestones.saver.ProjectProcurementMilestoneFormSaver;
import org.innovateuk.ifs.project.procurement.milestones.populator.ProjectProcurementMilestoneViewModelPopulator;
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

    @Autowired
    private ProjectProcurementMilestoneViewModelPopulator populator;

    @Autowired
    private ProjectProcurementMilestoneRestService projectProcurementMilestoneRestService;

    @Autowired
    private ProcurementMilestoneFormPopulator formPopulator;

    @GetMapping
    @PreAuthorize("hasPermission(#projectId, 'org.innovateuk.ifs.project.resource.ProjectCompositeId', 'ACCESS_FINANCE_CHECKS_SECTION')")
    public String viewMilestones(@PathVariable long projectId,
                                 @PathVariable long organisationId,
                                 @RequestParam(name = "editMilestones", defaultValue = "false") boolean editMilestones,
                                 UserResource userResource,
                                 Model model) {
        model.addAttribute("projectProcurementMilestoneApprovalForm", new ProjectProcurementMilestoneApprovalForm());
        ProcurementMilestonesForm form = formPopulator.populate(projectProcurementMilestoneRestService.getByProjectIdAndOrganisationId(projectId, organisationId).getSuccess());
        return viewProjectMilestones(projectId, organisationId, editMilestones, userResource, model, form);
    }

    private String viewProjectMilestones(long projectId, long organisationId, boolean editMilestones, UserResource userResource, Model model, ProcurementMilestonesForm form) {
        model.addAttribute("model", populator.populate(projectId, organisationId, userResource, editMilestones));
        return viewProjectSetupMilestones(model, userResource, form);
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
        Supplier<String> successView = redirectToFinanceChecks(projectId);

        return validationHandler.failNowOrSucceedWith(failureView, () -> {
            validationHandler.addAnyErrors(saver.save(form, projectId, organisationId));
            return validationHandler.failNowOrSucceedWith(failureView, () -> {
                validationHandler.addAnyErrors(
                        financeCheckRestService.resetPaymentMilestoneState(projectId, organisationId));
                        return validationHandler.failNowOrSucceedWith(failureView, successView);
            });
        });
    }

    @PostMapping(params = "remove_row")
    public String removeRowPost(Model model,
                                UserResource user,
                                @PathVariable long projectId,
                                @PathVariable long organisationId,
                                @ModelAttribute("form") ProcurementMilestonesForm form,
                                BindingResult bindingResult,
                                ValidationHandler validationHandler,
                                @RequestParam("remove_row") String removeId) {
        validationHandler.addAnyErrors(saver.removeRowFromForm(form, removeId));
        return viewProjectMilestones(projectId, organisationId, true, user, model, form);
    }

    @PostMapping(params = "add_row")
    public String addRowPost(Model model,
                             UserResource user,
                             @PathVariable long projectId,
                             @PathVariable long organisationId,
                             @ModelAttribute("form") ProcurementMilestonesForm form) {
        saver.addRowForm(form);
        return viewProjectMilestones(projectId, organisationId, true, user, model, form);
    }

    private Supplier<String> redirectToViewMilestones(long projectId, long organisationId) {
        return () -> String.format("redirect:/project/%d/finance-check/organisation/%d/procurement-milestones", projectId, organisationId);
    }

    private Supplier<String> redirectToFinanceChecks(long projectId) {
        return () -> String.format("redirect:/project/%d/finance-check", projectId);
    }

    @Override
    protected String getView() {
        return VIEW;
    }
}
