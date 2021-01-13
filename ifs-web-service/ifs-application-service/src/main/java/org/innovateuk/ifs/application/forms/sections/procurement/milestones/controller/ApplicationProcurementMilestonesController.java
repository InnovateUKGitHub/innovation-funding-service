package org.innovateuk.ifs.application.forms.sections.procurement.milestones.controller;

import org.innovateuk.ifs.application.ProcurementMilestones.AbstractProcurementMilestoneController;
import org.innovateuk.ifs.application.forms.sections.procurement.milestones.form.ProcurementMilestonesForm;
import org.innovateuk.ifs.application.forms.sections.procurement.milestones.populator.ApplicationProcurementMilestoneViewModelPopulator;
import org.innovateuk.ifs.application.forms.sections.procurement.milestones.populator.ProcurementMilestoneFormPopulator;
import org.innovateuk.ifs.application.forms.sections.procurement.milestones.saver.ApplicationProcurementMilestoneFormSaver;
import org.innovateuk.ifs.application.service.SectionStatusRestService;
import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.controller.ValidationHandler;
import org.innovateuk.ifs.form.resource.SectionType;
import org.innovateuk.ifs.procurement.milestone.service.ApplicationProcurementMilestoneRestService;
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
import java.util.function.Supplier;

import static org.innovateuk.ifs.application.forms.ApplicationFormUtil.APPLICATION_BASE_URL;

@Controller
@RequestMapping(APPLICATION_BASE_URL + "{applicationId}/form/procurement-milestones/organisation/{organisationId}/section/{sectionId}")
@PreAuthorize("hasAuthority('applicant')")
@SecuredBySpring(value = "UPDATE_PROCUREMENT_MILESTONE", description = "Applicants can update procurement milestones.")
public class ApplicationProcurementMilestonesController extends AbstractProcurementMilestoneController {
    private static final String VIEW = "application/sections/procurement-milestones/application-procurement-milestones";

    @Autowired
    private ProcurementMilestoneFormPopulator formPopulator;

    @Autowired
    private ApplicationProcurementMilestoneRestService restService;

    @Autowired
    private ApplicationProcurementMilestoneFormSaver saver;

    @Autowired
    private SectionStatusRestService sectionStatusRestService;

    @Autowired
    private ProcessRoleRestService processRoleRestService;

    @Autowired
    private ApplicationProcurementMilestoneViewModelPopulator viewModelPopulator;

    @GetMapping
    @PreAuthorize("hasAnyAuthority('applicant', 'support', 'innovation_lead', 'ifs_administrator', 'comp_admin', 'project_finance', 'stakeholder', 'external_finance', 'knowledge_transfer_adviser', 'supporter', 'assessor')")
    @SecuredBySpring(value = "VIEW_PROCUREMENT_MILESTONE", description = "Everyone view the milestone page, if they have permissions defined in data layer.")
    public String viewMilestones(@PathVariable long applicationId,
                                 @PathVariable long organisationId,
                                 @PathVariable long sectionId,
                                 UserResource user,
                                 Model model) {
        ProcurementMilestonesForm form = formPopulator.populate(restService.getByApplicationIdAndOrganisationId(applicationId, organisationId).getSuccess());
        model.addAttribute("form", form);
        return viewMilestones(model, form, user, applicationId, organisationId, sectionId);
    }

    @PostMapping
    public String saveMilestones(@PathVariable long applicationId,
                                 @PathVariable long organisationId,
                                 @PathVariable long sectionId,
                                 @ModelAttribute("form") ProcurementMilestonesForm form) {
        saver.save(form, applicationId, organisationId).getSuccess();
        return redirectToYourFinances(applicationId);
    }

    @PostMapping(params = "complete")
    public String complete(Model model,
                           UserResource user,
                           @PathVariable long applicationId,
                           @PathVariable long organisationId,
                           @PathVariable long sectionId,
                           @Valid @ModelAttribute("form") ProcurementMilestonesForm form,
                           BindingResult bindingResult,
                           ValidationHandler validationHandler) {
        Supplier<String> successView = () -> redirectToYourFinances(applicationId);
        Supplier<String> failureView = () -> viewMilestones(model, form, user, applicationId, organisationId, sectionId);
        return validationHandler.failNowOrSucceedWith(failureView, () -> {
            validationHandler.addAnyErrors(saver.save(form, applicationId, organisationId));
            return validationHandler.failNowOrSucceedWith(failureView, () -> {
                validationHandler.addAnyErrors(
                        sectionStatusRestService.markAsComplete(sectionId, applicationId, getProcessRoleId(applicationId, user.getId())).getSuccess());
                return validationHandler.failNowOrSucceedWith(failureView, successView);
            });
        });
    }

    @PostMapping(params = "edit")
    public String edit(Model model,
                       UserResource user,
                       @PathVariable long applicationId,
                       @PathVariable long organisationId,
                       @PathVariable long sectionId) {
        sectionStatusRestService.markAsInComplete(sectionId, applicationId, getProcessRoleId(applicationId, user.getId())).getSuccess();
        return String.format("redirect:/application/%d/form/procurement-milestones/organisation/%d/section/%d", applicationId, organisationId, sectionId);
    }

    @PostMapping(params = "remove_row")
    public String removeRowPost(Model model,
                                UserResource user,
                                @PathVariable long applicationId,
                                @PathVariable long organisationId,
                                @PathVariable long sectionId,
                                @ModelAttribute("form") ProcurementMilestonesForm form,
                                BindingResult bindingResult,
                                ValidationHandler validationHandler,
                                @RequestParam("remove_row") String removeId) {
        validationHandler.addAnyErrors(saver.removeRowFromForm(form, removeId));
        return viewMilestones(model, form, user, applicationId, organisationId, sectionId);
    }

    @PostMapping(params = "add_row")
    public String addRowPost(Model model,
                             UserResource user,
                             @PathVariable long applicationId,
                             @PathVariable long organisationId,
                             @PathVariable long sectionId,
                             @ModelAttribute("form") ProcurementMilestonesForm form) {

        saver.addRowForm(form);
        return viewMilestones(model, form, user, applicationId, organisationId, sectionId);
    }

    private String viewMilestones(Model model, ProcurementMilestonesForm form, UserResource user, long applicationId, long organisationId, long sectionId) {
        model.addAttribute("model", viewModelPopulator.populate(user, applicationId, organisationId, sectionId));
        return viewMilestonesPage(model, form, user);
    }

    private long getProcessRoleId(long applicationId, long userId) {
        return getProcessRole(applicationId, userId).getId();
    }

    private ProcessRoleResource getProcessRole(long applicationId, long userId) {
        return processRoleRestService.findProcessRole(userId, applicationId).getSuccess();
    }

    private String redirectToYourFinances(long applicationId) {
        return String.format("redirect:/application/%d/form/%s", applicationId, SectionType.FINANCE.name());
    }

    @Override
    protected String getView() {
        return VIEW;
    }
}
