package org.innovateuk.ifs.project.pendingpartner.controller;

import static java.lang.String.format;


import java.util.concurrent.Future;
import java.util.function.Supplier;
import javax.validation.Valid;
import org.innovateuk.ifs.application.forms.sections.yourorganisation.form.YourOrganisationWithoutGrowthTableForm;
import org.innovateuk.ifs.application.forms.sections.yourorganisation.form.YourOrganisationWithoutGrowthTableFormPopulator;
import org.innovateuk.ifs.application.forms.sections.yourorganisation.viewmodel.YourOrganisationViewModel;
import org.innovateuk.ifs.async.annotations.AsyncMethod;
import org.innovateuk.ifs.async.generation.AsyncAdaptor;
import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.controller.ValidationHandler;
import org.innovateuk.ifs.finance.resource.OrganisationFinancesWithoutGrowthTableResource;
import org.innovateuk.ifs.project.finance.service.ProjectYourOrganisationRestService;
import org.innovateuk.ifs.project.pendingpartner.populator.YourOrganisationViewModelPopulator;
import org.innovateuk.ifs.project.projectteam.PendingPartnerProgressRestService;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.service.UserRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * The Controller for the "Your organisation" page in the project setup process
 * when a a new partner has been invited and a growth table is not required.
 */
@Controller
@RequestMapping("/project/{projectId}/organisation/{organisationId}/your-organisation/without-growth-table")
public class ProjectYourOrganisationWithoutGrowthTableController extends AsyncAdaptor {

    private static final String VIEW_WITHOUT_GROWTH_TABLE_PAGE = "project/pending-partner-progress/your-organisation-without-growth-table";

    @Autowired
    private YourOrganisationViewModelPopulator viewModelPopulator;
    @Autowired
    private YourOrganisationWithoutGrowthTableFormPopulator withoutGrowthTableFormPopulator;
    @Autowired
    private UserRestService userRestService;
    @Autowired
    private ProjectYourOrganisationRestService yourOrganisationRestService;
    @Autowired
    private PendingPartnerProgressRestService pendingPartnerProgressRestService;

    @GetMapping
    @AsyncMethod
    @PreAuthorize("hasAnyAuthority('applicant')")
    @SecuredBySpring(value = "VIEW_YOUR_ORGANISATION", description = "Applicants and internal users can view the Your organisation page")
    public String viewPage(@PathVariable long projectId, @PathVariable long organisationId, Model model) {

        Future<YourOrganisationViewModel> viewModelRequest = async(() ->
            getViewModel(projectId, organisationId));

        Future<YourOrganisationWithoutGrowthTableForm> formRequest = async(() ->
            withoutGrowthTableFormPopulator.populate(yourOrganisationRestService.getOrganisationFinancesWithoutGrowthTable(projectId, organisationId).getSuccess()));

        model.addAttribute("model", viewModelRequest);
        model.addAttribute("form", formRequest);

        return VIEW_WITHOUT_GROWTH_TABLE_PAGE;
    }

    @PostMapping
    @PreAuthorize("hasAuthority('applicant')")
    @SecuredBySpring(value = "UPDATE_YOUR_ORGANISATION", description = "Applicants can update their organisation details")
    public String updateWithoutGrowthTable(@PathVariable long projectId, @PathVariable long organisationId, @ModelAttribute YourOrganisationWithoutGrowthTableForm form) {

        updateYourOrganisationWithoutGrowthTable(projectId, organisationId, form);
        return redirectToLandingPage(projectId, organisationId);
    }

    @PostMapping(params = {"mark-as-complete"})
    @PreAuthorize("hasAuthority('applicant')")
    @SecuredBySpring(value = "MARK_YOUR_ORGANISATION_AS_COMPLETE", description = "Applicants can mark their organisation details as complete")
    public String markAsCompleteWithoutGrowthTable(@PathVariable long projectId, @PathVariable long organisationId,
                                                   UserResource loggedInUser, @Valid @ModelAttribute("form") YourOrganisationWithoutGrowthTableForm form,
                                                   @SuppressWarnings("unused") BindingResult bindingResult, ValidationHandler validationHandler, Model model) {

        Supplier<String> failureHandler = () -> {
            YourOrganisationViewModel viewModel = getViewModel(projectId, organisationId);
            model.addAttribute("model", viewModel);
            model.addAttribute("form", form);
            return VIEW_WITHOUT_GROWTH_TABLE_PAGE;
        };

        Supplier<String> successHandler = () -> {
            updateYourOrganisationWithoutGrowthTable(projectId, organisationId, form);
            validationHandler.addAnyErrors(pendingPartnerProgressRestService.markYourOrganisationComplete(projectId, organisationId));
            return validationHandler.failNowOrSucceedWith(failureHandler, () -> redirectToLandingPage(projectId, organisationId));
        };

        return validationHandler.failNowOrSucceedWith(failureHandler, successHandler);
    }

    @PostMapping(params = "mark-as-incomplete")
    @PreAuthorize("hasAuthority('applicant')")
    @SecuredBySpring(value = "MARK_YOUR_ORGANISATION_AS_INCOMPLETE", description = "Applicants can mark their organisation details as incomplete")
    public String markAsIncomplete(@PathVariable long projectId, @PathVariable long organisationId) {

        pendingPartnerProgressRestService.markYourOrganisationIncomplete(projectId, organisationId);
        return redirectToViewPage(projectId, organisationId);
    }

    private void updateYourOrganisationWithoutGrowthTable(long projectId,
                                                       long organisationId,
                                                       YourOrganisationWithoutGrowthTableForm form) {

        OrganisationFinancesWithoutGrowthTableResource finances = new OrganisationFinancesWithoutGrowthTableResource(
            form.getOrganisationSize(),
            form.getTurnover(),
            form.getHeadCount());

        yourOrganisationRestService.updateOrganisationFinancesWithoutGrowthTable(projectId, organisationId, finances).
            getSuccess();
    }

    private YourOrganisationViewModel getViewModel(long projectId, long organisationId) {
        return viewModelPopulator.populate(projectId, organisationId);
    }

    private String redirectToViewPage(long projectId, long organisationId) {
        return format("redirect:/project/%d/organisation/%d/your-organisation/without-growth-table",
            projectId,
            organisationId);
    }

    private String redirectToLandingPage(long projectId, long organisationId) {
        return format("redirect:/project/%d/organisation/%d/pending-partner-progress",
            projectId,
            organisationId);
    }
}