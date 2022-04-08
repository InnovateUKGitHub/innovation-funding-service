package org.innovateuk.ifs.application.forms.sections.hecpcosts.controller;

import org.innovateuk.ifs.application.forms.hecpcosts.form.HorizonEuropeGuaranteeCostsForm;
import org.innovateuk.ifs.application.forms.sections.hecpcosts.populator.HorizonEuropeGuaranteeCostsFormPopulator;
import org.innovateuk.ifs.application.forms.sections.hecpcosts.saver.HorizonEuropeGuaranteeCostsSaver;
import org.innovateuk.ifs.application.forms.sections.yourprojectcosts.populator.YourProjectCostsViewModelPopulator;
import org.innovateuk.ifs.application.forms.sections.yourprojectcosts.viewmodel.YourProjectCostsViewModel;
import org.innovateuk.ifs.application.service.SectionStatusRestService;
import org.innovateuk.ifs.async.annotations.AsyncMethod;
import org.innovateuk.ifs.async.generation.AsyncAdaptor;
import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.controller.ValidationHandler;
import org.innovateuk.ifs.form.resource.SectionType;
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
@RequestMapping(APPLICATION_BASE_URL + "{applicationId}/form/hecp-costs/organisation/{organisationId}/section/{sectionId}")
@PreAuthorize("hasAuthority('applicant')")
@SecuredBySpring(value = "YOUR_PROJECT_COSTS_APPLICANT", description = "Applicants can all fill out the Horizon Europe Guarantee costs section of the application.")
public class HorizonEuropeGuaranteeCostsController extends AsyncAdaptor {
    private static final String VIEW = "application/sections/your-project-costs/hecp-costs";

    @Autowired
    private HorizonEuropeGuaranteeCostsFormPopulator formPopulator;

    @Autowired
    private YourProjectCostsViewModelPopulator viewModelPopulator;

    @Autowired
    private HorizonEuropeGuaranteeCostsSaver saver;

    @Autowired
    private SectionStatusRestService sectionStatusRestService;

    @Autowired
    private ProcessRoleRestService processRoleRestService;

    @GetMapping
    @PreAuthorize("hasAnyAuthority('applicant', 'support', 'innovation_lead', 'ifs_administrator', 'comp_admin', 'stakeholder')")
    @SecuredBySpring(value = "VIEW_HECP_COSTS", description = "Applicants and internal users can view the Your project costs page")
    public String viewHorizonEuropeGuaranteeCosts(Model model,
                                                  UserResource user,
                                                  @PathVariable long applicationId,
                                                  @PathVariable long organisationId,
                                                  @PathVariable long sectionId) {
        HorizonEuropeGuaranteeCostsForm form = formPopulator.populate(applicationId, organisationId);
        model.addAttribute("form", form);
        return viewHorizonEuropeGuaranteeCosts(user, model, applicationId, sectionId, organisationId);
    }

    @PostMapping
    @AsyncMethod
    public String saveYourProjectCosts(Model model,
                                       UserResource user,
                                       @PathVariable long applicationId,
                                       @PathVariable long organisationId,
                                       @PathVariable long sectionId,
                                       @ModelAttribute("form") HorizonEuropeGuaranteeCostsForm form) {
        saver.save(form, applicationId, organisationId);
        return redirectToYourFinances(applicationId);
    }

    @PostMapping(params = "complete")
    @AsyncMethod
    public String complete(Model model,
                           UserResource user,
                           @PathVariable long applicationId,
                           @PathVariable long organisationId,
                           @PathVariable long sectionId,
                           @Valid @ModelAttribute("form") HorizonEuropeGuaranteeCostsForm form,
                           BindingResult bindingResult,
                           ValidationHandler validationHandler) {
        Supplier<String> successView = () -> redirectToYourFinances(applicationId);
        Supplier<String> failureView = () -> viewHorizonEuropeGuaranteeCosts(user, model, applicationId, sectionId, organisationId);
        return validationHandler.failNowOrSucceedWith(failureView, () -> {
            validationHandler.addAnyErrors(saver.save(form, applicationId, organisationId));
            return validationHandler.failNowOrSucceedWith(failureView, () -> {
                validationHandler.addAnyErrors(
                        sectionStatusRestService.markAsComplete(sectionId, applicationId, getProcessRole(applicationId, user.getId()).getId()).getSuccess());
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
        return String.format("redirect:/application/%d/form/hecp-costs/organisation/%d/section/%d", applicationId, organisationId, sectionId);
    }

    private String redirectToYourFinances(long applicationId) {
        return String.format("redirect:/application/%d/form/%s", applicationId, SectionType.FINANCE.name());
    }

    private String viewHorizonEuropeGuaranteeCosts(UserResource user, Model model, long applicationId, long sectionId, long organisationId) {
        YourProjectCostsViewModel viewModel = viewModelPopulator.populate(applicationId, sectionId, organisationId, user);
        model.addAttribute("model", viewModel);
        return VIEW;
    }

    private long getProcessRoleId(long applicationId, long userId) {
        return getProcessRole(applicationId, userId).getId();
    }

    private ProcessRoleResource getProcessRole(long applicationId, long userId) {
        return processRoleRestService.findProcessRole(userId, applicationId).getSuccess();
    }
}