package org.innovateuk.ifs.application.forms.sections.yourorganisation.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.innovateuk.ifs.application.forms.sections.common.viewmodel.CommonYourFinancesViewModelPopulator;
import org.innovateuk.ifs.application.forms.sections.common.viewmodel.CommonYourProjectFinancesViewModel;
import org.innovateuk.ifs.application.forms.sections.yourorganisation.populator.ApplicationYourOrganisationViewModelPopulator;
import org.innovateuk.ifs.application.forms.sections.yourorganisation.viewmodel.YourOrganisationViewModel;
import org.innovateuk.ifs.application.service.SectionService;
import org.innovateuk.ifs.async.annotations.AsyncMethod;
import org.innovateuk.ifs.async.generation.AsyncAdaptor;
import org.innovateuk.ifs.commons.error.ValidationMessages;
import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.controller.ValidationHandler;
import org.innovateuk.ifs.user.resource.ProcessRoleResource;
import org.innovateuk.ifs.user.resource.Role;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.service.UserRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.concurrent.Future;
import java.util.function.Supplier;

import static org.innovateuk.ifs.application.forms.ApplicationFormUtil.APPLICATION_BASE_URL;

public abstract class AbstractYourOrganisationFormController<F> extends AsyncAdaptor {
    private static final String TEMPLATE = "application/sections/your-organisation/your-organisation";
    @Autowired
    private CommonYourFinancesViewModelPopulator commonFinancesViewModelPopulator;
    @Autowired
    private ApplicationYourOrganisationViewModelPopulator viewModelPopulator;
    @Autowired
    private SectionService sectionService;
    @Autowired
    private UserRestService userRestService;

    protected abstract String redirectToViewPage(long applicationId, long competitionId, long organisationId, long sectionId);
    protected abstract F populateForm(long applicationId, long organisationId);
    protected abstract String formFragment();
    protected abstract void update(long applicationId, long organisationId, F form);


    @GetMapping
    @AsyncMethod
    @PreAuthorize("hasAnyAuthority('applicant', 'support', 'innovation_lead', 'ifs_administrator', 'comp_admin', 'project_finance', 'stakeholder', 'external_finance')")
    @SecuredBySpring(value = "VIEW_YOUR_ORGANISATION", description = "Applicants and internal users can view the Your organisation page")
    public String viewPage(
            @PathVariable long applicationId,
            @PathVariable long competitionId,
            @PathVariable long organisationId,
            @PathVariable long sectionId,
            UserResource loggedInUser,
            Model model) {

        Future<CommonYourProjectFinancesViewModel> commonViewModelRequest = async(() ->
                getCommonFinancesViewModel(applicationId, sectionId, organisationId, loggedInUser.isInternalUser() || loggedInUser.hasRole(Role.EXTERNAL_FINANCE)));

        Future<YourOrganisationViewModel> viewModelRequest = async(() ->
                getViewModel(applicationId, competitionId, organisationId));

        Future<F> formRequest = async(() ->
            populateForm(applicationId, organisationId)
        );

        model.addAttribute("commonFinancesModel", commonViewModelRequest);
        model.addAttribute("model", viewModelRequest);
        model.addAttribute("form", formRequest);
        model.addAttribute("formFragment", formFragment());

        return TEMPLATE;
    }

    @PostMapping
    @PreAuthorize("hasAuthority('applicant')")
    @SecuredBySpring(value = "UPDATE_YOUR_ORGANISATION", description = "Applicants can update their organisation funding details")
    public String updateWithGrowthTable(
            @PathVariable long applicationId,
            @PathVariable long organisationId,
            @ModelAttribute F form) {

        update(applicationId, organisationId, form);
        return redirectToYourFinances(applicationId);
    }

    @PostMapping(value = "/auto-save")
    @PreAuthorize("hasAuthority('applicant')")
    @SecuredBySpring(value = "UPDATE_YOUR_ORGANISATION", description = "Applicants can update their organisation funding details")
    public @ResponseBody
    JsonNode autosaveWithGrowthTable(
            @PathVariable long applicationId,
            @PathVariable long organisationId,
            @ModelAttribute F form) {

        update(applicationId, organisationId, form);
        return new ObjectMapper().createObjectNode();
    }

    @PostMapping(params = {"mark-as-complete"})
    @PreAuthorize("hasAuthority('applicant')")
    @SecuredBySpring(value = "MARK_YOUR_ORGANISATION_AS_COMPLETE", description = "Applicants can mark their organisation funding details as complete")
    public String markAsCompleteWithGrowthTable(
            @PathVariable long applicationId,
            @PathVariable long competitionId,
            @PathVariable long organisationId,
            @PathVariable long sectionId,
            UserResource loggedInUser,
            @Valid @ModelAttribute("form") F form,
            @SuppressWarnings("unused") BindingResult bindingResult,
            ValidationHandler validationHandler,
            Model model) {

        Supplier<String> failureHandler = () -> {
            CommonYourProjectFinancesViewModel commonViewModel = getCommonFinancesViewModel(applicationId, sectionId, organisationId, false);
            YourOrganisationViewModel viewModel = getViewModel(applicationId, competitionId, organisationId);
            model.addAttribute("commonFinancesModel", commonViewModel);
            model.addAttribute("model", viewModel);
            model.addAttribute("form", form);
            model.addAttribute("formFragment", formFragment());
            return TEMPLATE;
        };

        Supplier<String> successHandler = () -> {

            update(applicationId, organisationId, form);

            ProcessRoleResource processRole = userRestService.findProcessRole(loggedInUser.getId(), applicationId).getSuccess();
            ValidationMessages validationMessages = sectionService.markAsComplete(sectionId, applicationId, processRole.getId());
            validationHandler.addAnyErrors(validationMessages);

            return validationHandler.failNowOrSucceedWith(failureHandler, () -> redirectToYourFinances(applicationId));
        };

        return validationHandler.failNowOrSucceedWith(failureHandler, successHandler);
    }

    @PostMapping(params = "mark-as-incomplete")
    @PreAuthorize("hasAuthority('applicant')")
    @SecuredBySpring(value = "MARK_YOUR_ORGANISATION_AS_INCOMPLETE", description = "Applicants can mark their organisation funding details as incomplete")
    public String markAsIncomplete(
            @PathVariable long applicationId,
            @PathVariable long competitionId,
            @PathVariable long organisationId,
            @PathVariable long sectionId,
            UserResource loggedInUser) {

        ProcessRoleResource processRole = userRestService.findProcessRole(loggedInUser.getId(), applicationId).getSuccess();
        sectionService.markAsInComplete(sectionId, applicationId, processRole.getId());
        return redirectToViewPage(applicationId, competitionId, organisationId, sectionId);
    }

    private YourOrganisationViewModel getViewModel(long applicationId, long competitionId, long organisationId) {
        return viewModelPopulator.populate(applicationId, competitionId, organisationId);
    }

    private CommonYourProjectFinancesViewModel getCommonFinancesViewModel(long applicationId, long sectionId, long organisationId, boolean internalUser) {
        return commonFinancesViewModelPopulator.populate(organisationId, applicationId, sectionId, internalUser);
    }

    private String redirectToYourFinances(long applicationId) {
        // IFS-4848 - we're constructing this URL in a few places - maybe a NavigationUtil?
        return "redirect:" + String.format("%s%d/form/FINANCE", APPLICATION_BASE_URL, applicationId);
    }
}
