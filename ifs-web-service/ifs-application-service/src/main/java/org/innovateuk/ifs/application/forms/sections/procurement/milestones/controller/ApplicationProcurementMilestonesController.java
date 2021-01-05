package org.innovateuk.ifs.application.forms.sections.procurement.milestones.controller;

import org.innovateuk.ifs.application.forms.sections.procurement.milestones.form.ProcurementMilestonesForm;
import org.innovateuk.ifs.application.forms.sections.procurement.milestones.populator.ProcurementMilestoneFormPopulator;
import org.innovateuk.ifs.application.forms.sections.procurement.milestones.saver.ApplicationProcurementMilestoneFormSaver;
import org.innovateuk.ifs.application.forms.sections.procurement.milestones.viewmodel.ApplicationProcurementMilestonesViewModel;
import org.innovateuk.ifs.application.service.ApplicationRestService;
import org.innovateuk.ifs.application.service.SectionService;
import org.innovateuk.ifs.application.service.SectionStatusRestService;
import org.innovateuk.ifs.controller.ValidationHandler;
import org.innovateuk.ifs.finance.resource.ApplicationFinanceResource;
import org.innovateuk.ifs.finance.service.ApplicationFinanceRestService;
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
public class ApplicationProcurementMilestonesController {
    private static final String VIEW = "application/sections/procurement-milestones/procurement-milestones";


    @Autowired
    private ProcurementMilestoneFormPopulator formPopulator;

    @Autowired
    private ApplicationProcurementMilestoneRestService restService;

    @Autowired
    private ApplicationRestService applicationRestService;

    @Autowired
    private ApplicationProcurementMilestoneFormSaver applicationProcurementMilestoneFormSaver;

    @Autowired
    private SectionStatusRestService sectionStatusRestService;

    @Autowired
    private ProcessRoleRestService processRoleRestService;

    @Autowired
    private SectionService sectionService;

    @Autowired
    private ApplicationFinanceRestService applicationFinanceRestService;

    @GetMapping
    public String viewMilestones(@PathVariable long applicationId,
                                 @PathVariable long organisationId,
                                 @PathVariable long sectionId,
                                 Model model) {
        model.addAttribute("form", formPopulator.populate(restService.getByApplicationIdAndOrganisationId(applicationId, organisationId).getSuccess()));
        return viewMilestones(model, applicationId, organisationId, sectionId);
    }

    private String viewMilestones(Model model, long applicationId, long organisationId, long sectionId) {
        ApplicationFinanceResource finance = applicationFinanceRestService.getFinanceDetails(applicationId, organisationId).getSuccess();
        model.addAttribute("model", new ApplicationProcurementMilestonesViewModel(applicationRestService.getApplicationById(applicationId).getSuccess(),
                finance.getTotalFundingSought(),
                String.format("/application/%d/form/FINANCE/%d", applicationId, organisationId),
                sectionService.getCompleted(applicationId, organisationId).contains(sectionId)));
        return VIEW;
    }

    @PostMapping
    public String saveMilestones(@PathVariable long applicationId,
                                 @PathVariable long organisationId,
                                 @PathVariable long sectionId,
                                 @ModelAttribute("form") ProcurementMilestonesForm form,
                                 BindingResult bindingResult,
                                 ValidationHandler validationHandler) {
        applicationProcurementMilestoneFormSaver.save(form, applicationId, organisationId).getSuccess();
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
        Supplier<String> failureView = () -> viewMilestones(model, applicationId, organisationId, sectionId);
        return validationHandler.failNowOrSucceedWith(failureView, () -> {
            validationHandler.addAnyErrors(applicationProcurementMilestoneFormSaver.save(form, applicationId, organisationId));
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

    private long getProcessRoleId(long applicationId, long userId) {
        return getProcessRole(applicationId, userId).getId();
    }

    private ProcessRoleResource getProcessRole(long applicationId, long userId) {
        return processRoleRestService.findProcessRole(userId, applicationId).getSuccess();
    }

    /*

    @PostMapping
    @AsyncMethod
    public String saveYourProjectCosts(Model model,
                                       UserResource user,
                                       @PathVariable long applicationId,
                                       @PathVariable long sectionId,
                                       @ModelAttribute("form") YourProjectCostsForm form,
                                       BindingResult bindingResult) {
        saver.save(form, applicationId, user);
        return redirectToYourFinances(applicationId);
    }

    @PostMapping(params = "complete")
    @AsyncMethod
    public String complete(Model model,
                           UserResource user,
                           @PathVariable long applicationId,
                           @PathVariable long organisationId,
                           @PathVariable long sectionId,
                           @Valid @ModelAttribute("form") YourProjectCostsForm form,
                           BindingResult bindingResult,
                           ValidationHandler validationHandler) {
        Supplier<String> successView = () -> redirectToYourFinances(applicationId);
        Supplier<String> failureView = () -> viewYourProjectCosts(form, user, model, applicationId, sectionId, organisationId);
        validator.validate(applicationId, form, validationHandler);
        return validationHandler.failNowOrSucceedWith(failureView, () -> {
            validationHandler.addAnyErrors(saver.save(form, applicationId, user));
            return validationHandler.failNowOrSucceedWith(failureView, () -> {
                validationHandler.addAnyErrors(
                        sectionStatusRestService.markAsComplete(sectionId, applicationId, getProcessRole(applicationId, user.getId()).getId()).getSuccess());
                return validationHandler.failNowOrSucceedWith(failureView, successView);
            });
        });
    }



    @PostMapping(params = "remove_cost")
    public String removeRowPost(Model model,
                                UserResource user,
                                @PathVariable long applicationId,
                                @PathVariable long organisationId,
                                @PathVariable long sectionId,
                                @ModelAttribute("form") YourProjectCostsForm form,
                                @RequestParam("remove_cost") String removeId) {
        saver.removeRowFromForm(form, removeId);
        return viewYourProjectCosts(form, user, model, applicationId, sectionId, organisationId);
    }

    @PostMapping(params = "add_cost")
    public String addRowPost(Model model,
                             UserResource user,
                             @PathVariable long applicationId,
                             @PathVariable long organisationId,
                             @PathVariable long sectionId,
                             @ModelAttribute("form") YourProjectCostsForm form,
                             @RequestParam("add_cost") FinanceRowType rowType) throws InstantiationException, IllegalAccessException {

        saver.addRowForm(form, rowType);
        return viewYourProjectCosts(form, user, model, applicationId, sectionId, organisationId);
    }
     */

    private String redirectToYourFinances(long applicationId) {
        return String.format("redirect:/application/%d/form/%s", applicationId, SectionType.FINANCE.name());
    }
}
