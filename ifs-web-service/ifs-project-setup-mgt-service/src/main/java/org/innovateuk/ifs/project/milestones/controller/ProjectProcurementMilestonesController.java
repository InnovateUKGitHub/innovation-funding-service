package org.innovateuk.ifs.project.milestones.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.innovateuk.ifs.application.forms.sections.procurement.milestones.form.ProcurementMilestonesForm;
import org.innovateuk.ifs.application.forms.sections.procurement.milestones.populator.ProcurementMilestoneFormPopulator;
import org.innovateuk.ifs.application.forms.sections.procurement.milestones.validator.ProcurementMilestoneFormValidator;
import org.innovateuk.ifs.application.procurement.milestones.AbstractProcurementMilestoneController;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.controller.ValidationHandler;
import org.innovateuk.ifs.procurement.milestone.service.ProjectProcurementMilestoneRestService;
import org.innovateuk.ifs.project.finance.service.FinanceCheckRestService;
import org.innovateuk.ifs.project.finance.service.ProjectFinanceRestService;
import org.innovateuk.ifs.project.milestones.form.ProjectProcurementMilestoneApprovalForm;
import org.innovateuk.ifs.project.milestones.saver.ProjectProcurementMilestoneFormSaver;
import org.innovateuk.ifs.project.procurement.milestones.populator.ProjectProcurementMilestoneViewModelPopulator;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Optional;
import java.util.function.Supplier;

import static org.innovateuk.ifs.controller.ErrorToObjectErrorConverterFactory.asGlobalErrors;
import static org.innovateuk.ifs.controller.ErrorToObjectErrorConverterFactory.fieldErrorsToFieldErrors;

@Controller
@RequestMapping("/project/{projectId}/finance-check/organisation/{organisationId}/procurement-milestones")
@PreAuthorize("hasPermission(#projectId, 'org.innovateuk.ifs.project.resource.ProjectCompositeId', 'ACCESS_FINANCE_CHECKS_SECTION')")
@SecuredBySpring(value = "UPDATE_PROCUREMENT_MILESTONE", description = "project finance can update procurement milestones.")
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

    @Autowired
    private ProcurementMilestoneFormValidator validator;

    @Autowired
    private ProjectFinanceRestService projectFinanceRestService;

    @GetMapping
    public String viewMilestones(@PathVariable long projectId,
                                 @PathVariable long organisationId,
                                 @RequestParam(name = "editMilestones", defaultValue = "false") boolean editMilestones,
                                 UserResource userResource,
                                 Model model) {
        ProcurementMilestonesForm form = formPopulator.populate(projectProcurementMilestoneRestService.getByProjectIdAndOrganisationId(projectId, organisationId).getSuccess());
        return viewProjectMilestones(projectId, organisationId, editMilestones, userResource, model, form, null);
    }

    private String viewProjectMilestones(long projectId, long organisationId, boolean editMilestones, UserResource userResource, Model model, ProcurementMilestonesForm form, ProjectProcurementMilestoneApprovalForm approvalForm) {
        model.addAttribute("projectProcurementMilestoneApprovalForm", approvalForm != null ? approvalForm : new ProjectProcurementMilestoneApprovalForm());
        model.addAttribute("model", populator.populate(projectId, organisationId, userResource, editMilestones, true));
        return viewProjectSetupMilestones(model, userResource, form);
    }

    @PostMapping(params = "approve")
    public String approvePaymentMilestones(@PathVariable long projectId,
                                           @PathVariable long organisationId,
                                           @ModelAttribute("projectProcurementMilestoneApprovalForm") ProjectProcurementMilestoneApprovalForm projectProcurementMilestoneApprovalForm,
                                           @SuppressWarnings("unused") BindingResult bindingResult,
                                           ValidationHandler validationHandler,
                                           Model model,
                                           UserResource user) {
        ProcurementMilestonesForm form = formPopulator.populate(projectProcurementMilestoneRestService.getByProjectIdAndOrganisationId(projectId, organisationId).getSuccess());
        validator.validate(form, projectFinanceRestService.getProjectFinance(projectId, organisationId).getSuccess(), validationHandler);
        Supplier<String> view = () -> viewProjectMilestones(projectId,organisationId,false, user, model, form, projectProcurementMilestoneApprovalForm);
        return validationHandler.failNowOrSucceedWith(view, () -> {
            RestResult<Void> approvePaymentMilestoneState = financeCheckRestService.approvePaymentMilestoneState(projectId, organisationId);
            return validationHandler
                    .addAnyErrors(approvePaymentMilestoneState)
                    .failNowOrSucceedWith(view, view);
        });
    }

    @PreAuthorize("hasPermission(#projectId, 'org.innovateuk.ifs.project.resource.ProjectCompositeId', 'ACCESS_FINANCE_CHECKS_SECTION')")
    @PostMapping(params = "reset-milestones")
    public String resetPaymentMilestones(@PathVariable long projectId,
                                           @PathVariable long organisationId,
                                           @ModelAttribute("projectProcurementMilestoneApprovalForm") ProjectProcurementMilestoneApprovalForm approvalsForm,
                                           @SuppressWarnings("unused") BindingResult bindingResult,
                                           ValidationHandler validationHandler,
                                           Model model,
                                           UserResource user) {
        ProcurementMilestonesForm form = formPopulator.populate(projectProcurementMilestoneRestService.getByProjectIdAndOrganisationId(projectId, organisationId).getSuccess());

        Supplier<String> view = () -> viewProjectMilestones(projectId, organisationId, false, user, model, form, approvalsForm);

        if (StringUtils.isEmpty(approvalsForm.getRetractionReason())) {
            bindingResult.addError(new FieldError("projectProcurementMilestoneApprovalForm", "retractionReason", "Enter a reason for the reset."));
            return view.get();
        }

        RestResult<Void> resetPaymentMilestoneState = financeCheckRestService.resetPaymentMilestoneState(projectId, organisationId, approvalsForm.getRetractionReason());
        return validationHandler
                .addAnyErrors(resetPaymentMilestoneState)
                .failNowOrSucceedWith(view, view);
    }

    @PreAuthorize("hasPermission(#projectId, 'org.innovateuk.ifs.project.resource.ProjectCompositeId', 'ACCESS_FINANCE_CHECKS_SECTION')")
    @PostMapping(params = "save")
    public String saveMilestones(@PathVariable long projectId,
                                 @PathVariable long organisationId,
                                 @Valid @ModelAttribute("form") ProcurementMilestonesForm form,
                                 @SuppressWarnings("unused") BindingResult bindingResult,
                                 ValidationHandler validationHandler,
                                 Model model,
                                 UserResource user) {
        validator.validate(form, projectFinanceRestService.getProjectFinance(projectId, organisationId).getSuccess(), validationHandler);
        Supplier<String> failureView = () -> viewProjectMilestones(projectId, organisationId, true, user, model,form, null);
        Supplier<String> successView = redirectToFinanceChecks(projectId);

        return validationHandler.failNowOrSucceedWith(failureView, () -> {
            validationHandler.addAnyErrors(saver.save(form, projectId, organisationId));
            return validationHandler.failNowOrSucceedWith(failureView, () -> {
                validationHandler.addAnyErrors(
                        financeCheckRestService.resetPaymentMilestoneState(projectId, organisationId, null));
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
        return viewProjectMilestones(projectId, organisationId, true, user, model, form, null);
    }

    @PostMapping(params = "add_row")
    public String addRowPost(Model model,
                             UserResource user,
                             @PathVariable long projectId,
                             @PathVariable long organisationId,
                             @ModelAttribute("form") ProcurementMilestonesForm form) {
        saver.addRowForm(form);
        return viewProjectMilestones(projectId, organisationId, true, user, model, form, null);
    }

    @PostMapping("/remove-row/{rowId}")
    public @ResponseBody
    JsonNode ajaxRemoveRow(UserResource user,
                           @PathVariable long projectId,
                           @PathVariable long organisationId,
                           @PathVariable String rowId) {
        saver.removeRow(rowId);
        return new ObjectMapper().createObjectNode();
    }

    @PostMapping("/add-row")
    public String ajaxAddRow(Model model,
                             UserResource userResource,
                             @PathVariable long projectId,
                             @PathVariable long organisationId) {
        model.addAttribute("model", populator.populate(projectId, organisationId, userResource, true, true));
        return addAjaxRow(model);
    }

    protected String viewProjectSetupMilestones(Model model, UserResource userResource, ProcurementMilestonesForm form) {
        model.addAttribute("form", form);
        return viewMilestonesPage(model, form, userResource);
    }

    private Supplier<String> redirectToFinanceChecks(long projectId) {
        return () -> String.format("redirect:/project/%d/finance-check", projectId);
    }

    @Override
    protected String getView() {
        return VIEW;
    }

    @Override
    protected ProjectProcurementMilestoneFormSaver getSaver() {
        return saver;
    }
}
