package org.innovateuk.ifs.application.forms.sections.h2020costs.controller;

import org.innovateuk.ifs.application.forms.sections.h2020costs.form.Horizon2020CostsForm;
import org.innovateuk.ifs.application.forms.sections.h2020costs.populator.Horizon2020CostsFormPopulator;
import org.innovateuk.ifs.application.forms.sections.h2020costs.saver.Horizon2020CostsSaver;
import org.innovateuk.ifs.application.forms.sections.yourprojectcosts.populator.YourProjectCostsViewModelPopulator;
import org.innovateuk.ifs.application.forms.sections.yourprojectcosts.saver.YourProjectCostsCompleter;
import org.innovateuk.ifs.application.forms.sections.yourprojectcosts.viewmodel.YourProjectCostsViewModel;
import org.innovateuk.ifs.application.service.SectionStatusRestService;
import org.innovateuk.ifs.async.annotations.AsyncMethod;
import org.innovateuk.ifs.async.generation.AsyncAdaptor;
import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.controller.ValidationHandler;
import org.innovateuk.ifs.finance.service.OverheadFileRestService;
import org.innovateuk.ifs.form.resource.SectionType;
import org.innovateuk.ifs.origin.ApplicationSummaryOrigin;
import org.innovateuk.ifs.user.resource.ProcessRoleResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.service.UserRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.function.Supplier;

import static org.innovateuk.ifs.application.forms.ApplicationFormUtil.APPLICATION_BASE_URL;
import static org.innovateuk.ifs.origin.BackLinkUtil.buildOriginQueryString;

@Controller
@RequestMapping(APPLICATION_BASE_URL + "{applicationId}/form/horizon-2020-costs/organisation/{organisationId}/section/{sectionId}")
@PreAuthorize("hasAuthority('applicant')")
@SecuredBySpring(value = "YOUR_PROJECT_COSTS_APPLICANT", description = "Applicants can all fill out the horizon 2020 costs section of the application.")
public class Horizon2020CostsController extends AsyncAdaptor {
    private static final String VIEW = "application/horizon-2020-costs";

    @Autowired
    private Horizon2020CostsFormPopulator formPopulator;

    @Autowired
    private YourProjectCostsViewModelPopulator viewModelPopulator;

    @Autowired
    private Horizon2020CostsSaver saver;

    @Autowired
    private SectionStatusRestService sectionStatusRestService;

    @Autowired
    private UserRestService userRestService;

    @Autowired
    private OverheadFileRestService overheadFileRestService;

    @Autowired
    private YourProjectCostsCompleter completeSectionAction;

    @GetMapping
    @PreAuthorize("hasAnyAuthority('applicant', 'support', 'innovation_lead', 'ifs_administrator', 'comp_admin', 'project_finance', 'stakeholder')")
    @SecuredBySpring(value = "VIEW_H2020_COSTS", description = "Applicants and internal users can view the Your project costs page")
    public String viewHorizon2020Costs(Model model,
                                       UserResource user,
                                       @PathVariable long applicationId,
                                       @PathVariable long organisationId,
                                       @PathVariable long sectionId,
                                       @RequestParam(value = "origin", required = false) String origin,
                                       @RequestParam MultiValueMap<String, String> queryParams) {
        String originQuery = "";
        if (origin != null) {
            originQuery = buildOriginQueryString(ApplicationSummaryOrigin.valueOf(origin), queryParams);
        }
        Horizon2020CostsForm form = formPopulator.populate(applicationId, organisationId);
        model.addAttribute("form", form);
        return viewHorizon2020Costs(user, model, applicationId, sectionId, organisationId, originQuery);
    }

    @PostMapping
    @AsyncMethod
    public String saveYourProjectCosts(Model model,
                                       UserResource user,
                                       @PathVariable long applicationId,
                                       @PathVariable long organisationId,
                                       @PathVariable long sectionId,
                                       @ModelAttribute("form") Horizon2020CostsForm form) {
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
                           @Valid @ModelAttribute("form") Horizon2020CostsForm form,
                           BindingResult bindingResult,
                           ValidationHandler validationHandler) {
        Supplier<String> successView = () -> redirectToYourFinances(applicationId);
        Supplier<String> failureView = () -> viewHorizon2020Costs(user, model, applicationId, sectionId, organisationId, "");
        return validationHandler.failNowOrSucceedWith(failureView, () -> {
            validationHandler.addAnyErrors(saver.save(form, applicationId, organisationId));
            return validationHandler.failNowOrSucceedWith(failureView, () -> {
                validationHandler.addAnyErrors(completeSectionAction.markAsComplete(sectionId, applicationId, getProcessRole(applicationId, user.getId())));
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
        return String.format("redirect:/application/%d/form/horizon-2020-costs/organisation/%d/section/%d", applicationId, organisationId, sectionId);
    }

    private String redirectToYourFinances(long applicationId) {
        return String.format("redirect:/application/%d/form/%s", applicationId, SectionType.FINANCE.name());
    }

    private String viewHorizon2020Costs(UserResource user, Model model, long applicationId, long sectionId, long organisationId, String originQuery) {
        YourProjectCostsViewModel viewModel = viewModelPopulator.populate(applicationId, sectionId, organisationId, user.isInternalUser(), originQuery);
        model.addAttribute("model", viewModel);
        return VIEW;
    }

    private long getProcessRoleId(long applicationId, long userId) {
        return getProcessRole(applicationId, userId).getId();
    }

    private ProcessRoleResource getProcessRole(long applicationId, long userId) {
        return userRestService.findProcessRole(userId, applicationId).getSuccess();
    }

}
