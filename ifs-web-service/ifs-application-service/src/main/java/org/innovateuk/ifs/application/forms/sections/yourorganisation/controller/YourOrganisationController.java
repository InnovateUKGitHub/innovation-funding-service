package org.innovateuk.ifs.application.forms.sections.yourorganisation.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.innovateuk.ifs.application.forms.sections.yourorganisation.form.YourOrganisationForm;
import org.innovateuk.ifs.application.forms.sections.yourorganisation.form.YourOrganisationFormPopulator;
import org.innovateuk.ifs.application.forms.sections.yourorganisation.viewmodel.YourOrganisationViewModel;
import org.innovateuk.ifs.application.forms.sections.yourorganisation.viewmodel.YourOrganisationViewModelPopulator;
import org.innovateuk.ifs.application.service.SectionService;
import org.innovateuk.ifs.async.annotations.AsyncMethod;
import org.innovateuk.ifs.async.generation.AsyncAdaptor;
import org.innovateuk.ifs.commons.error.Error;
import org.innovateuk.ifs.commons.error.ValidationMessages;
import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.controller.ValidationHandler;
import org.innovateuk.ifs.finance.resource.ApplicationFinanceResource;
import org.innovateuk.ifs.finance.service.ApplicationFinanceRestService;
import org.innovateuk.ifs.user.resource.ProcessRoleResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.service.UserRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.concurrent.Future;
import java.util.function.Supplier;

import static java.util.Collections.emptyList;
import static org.innovateuk.ifs.application.forms.ApplicationFormUtil.APPLICATION_BASE_URL;

/**
 * The Controller for the "Your project location" page in the Application Form process.
 */
@Controller
@RequestMapping(APPLICATION_BASE_URL + "{applicationId}/form/your-organisation/organisation/{organisationId}/section/{sectionId}")
public class YourOrganisationController extends AsyncAdaptor {

    private static final String VIEW_PAGE = "application/sections/your-organisation/your-organisation";
    private static final int MINIMUM_POSTCODE_LENGTH = 3;
    private static final int MAXIMUM_POSTCODE_LENGTH = 10;

    private YourOrganisationViewModelPopulator viewModelPopulator;
    private YourOrganisationFormPopulator formPopulator;
    private ApplicationFinanceRestService applicationFinanceRestService;
    private SectionService sectionService;
    private UserRestService userRestService;

    @Autowired
    YourOrganisationController(
            YourOrganisationViewModelPopulator viewModelPopulator,
            YourOrganisationFormPopulator formPopulator,
            ApplicationFinanceRestService applicationFinanceRestService,
            SectionService sectionService,
            UserRestService userRestService) {

        this.viewModelPopulator = viewModelPopulator;
        this.formPopulator = formPopulator;
        this.applicationFinanceRestService = applicationFinanceRestService;
        this.sectionService = sectionService;
        this.userRestService = userRestService;
    }

    // for ByteBuddy
    YourOrganisationController() {
        this.viewModelPopulator = null;
        this.formPopulator = null;
        this.applicationFinanceRestService = null;
        this.sectionService = null;
        this.userRestService = null;
    }

    @GetMapping
    @AsyncMethod
    @PreAuthorize("hasAnyAuthority('applicant', 'support', 'innovation_lead', 'ifs_administrator', 'comp_admin', 'project_finance', 'stakeholder')")
    @SecuredBySpring(value = "VIEW_YOUR_ORGANISATION", description = "Applicants and internal users can view the Your organisation page")
    public String viewPage(
            @PathVariable("applicationId") long applicationId,
            @PathVariable("organisationId") long organisationId,
            @PathVariable("sectionId") long sectionId,
            UserResource loggedInUser,
            Model model) {

        Future<YourOrganisationViewModel> viewModelRequest = async(() ->
                getViewModel(applicationId, sectionId, organisationId, loggedInUser.isInternalUser()));

        Future<YourOrganisationForm> formRequest = async(() ->
                formPopulator.populate(applicationId, organisationId));

        model.addAttribute("model", viewModelRequest);
        model.addAttribute("form", formRequest);

        return VIEW_PAGE;
    }

    @PostMapping
    @PreAuthorize("hasAuthority('applicant')")
    @SecuredBySpring(value = "UPDATE_YOUR_ORGANISATION", description = "Applicants can update their organisation funding details")
    public String update(
            @PathVariable("applicationId") long applicationId,
            @PathVariable("organisationId") long organisationId,
            @ModelAttribute YourOrganisationForm form) {

        updateYourOrganisation(applicationId, organisationId, form);
        return redirectToYourFinances(applicationId);
    }

    @PostMapping("/auto-save")
    @PreAuthorize("hasAuthority('applicant')")
    @SecuredBySpring(value = "UPDATE_YOUR_ORGANISATION", description = "Applicants can update their organisation funding details")
    public @ResponseBody JsonNode autosave(
            @PathVariable("applicationId") long applicationId,
            @PathVariable("organisationId") long organisationId,
            @ModelAttribute YourOrganisationForm form) {

        update(applicationId, organisationId, form);
        return new ObjectMapper().createObjectNode();
    }

    @PostMapping(params = "mark-as-complete")
    @PreAuthorize("hasAuthority('applicant')")
    @SecuredBySpring(value = "MARK_YOUR_ORGANISATION_AS_COMPLETE", description = "Applicants can mark their organisation funding details as complete")
    public String markAsComplete(
            @PathVariable("applicationId") long applicationId,
            @PathVariable("organisationId") long organisationId,
            @PathVariable("sectionId") long sectionId,
            UserResource loggedInUser,
            @Valid @ModelAttribute("form") YourOrganisationForm form,
            @SuppressWarnings("unused") BindingResult bindingResult,
            ValidationHandler validationHandler,
            Model model) {

        Supplier<String> failureHandler = () -> {
            YourOrganisationViewModel viewModel = getViewModel(applicationId, sectionId, organisationId, false);
            model.addAttribute("model", viewModel);
            model.addAttribute("form", form);
            return VIEW_PAGE;
        };

        Supplier<String> successHandler = () -> {

            updateYourOrganisation(applicationId, organisationId, form);

            ProcessRoleResource processRole = userRestService.findProcessRole(loggedInUser.getId(), applicationId).getSuccess();
            List<ValidationMessages> validationMessages = sectionService.markAsComplete(sectionId, applicationId, processRole.getId());
            validationMessages.forEach(validationHandler::addAnyErrors);

            return validationHandler.failNowOrSucceedWith(failureHandler, () -> redirectToYourFinances(applicationId));
        };

        return validationHandler.
                addAnyErrors(validateYourOrganisation(form.getPostcode())).
                failNowOrSucceedWith(failureHandler, successHandler);
    }

    @PostMapping(params = "mark-as-incomplete")
    @PreAuthorize("hasAuthority('applicant')")
    @SecuredBySpring(value = "MARK_YOUR_ORGANISATION_AS_INCOMPLETE", description = "Applicants can mark their organisation funding details as incomplete")
    public String markAsIncomplete(
            @PathVariable("applicationId") long applicationId,
            @PathVariable("organisationId") long organisationId,
            @PathVariable("sectionId") long sectionId,
            UserResource loggedInUser) {

        ProcessRoleResource processRole = userRestService.findProcessRole(loggedInUser.getId(), applicationId).getSuccess();
        sectionService.markAsInComplete(sectionId, applicationId, processRole.getId());
        return redirectToViewPage(applicationId, organisationId, sectionId);
    }

    private void updateYourOrganisation(long applicationId,
                                        long organisationId,
                                        YourOrganisationForm form) {

        ApplicationFinanceResource finance =
                applicationFinanceRestService.getApplicationFinance(applicationId, organisationId).getSuccess();

        finance.setWorkPostcode(form.getPostcode());

        applicationFinanceRestService.update(finance.getId(), finance).getSuccess();
    }

    private List<Error> validateYourOrganisation(String postcode) {
        return emptyList();
    }

    private YourOrganisationViewModel getViewModel(long applicationId, long sectionId, long organisationId, boolean internalUser) {
        return viewModelPopulator.populate(organisationId, applicationId, sectionId, internalUser);
    }

    private String redirectToViewPage(long applicationId, long organisationId, long sectionId) {
        return "redirect:" + APPLICATION_BASE_URL +
                String.format("%d/form/your-organisation/organisation/%d/section/%d",
                        applicationId,
                        organisationId,
                        sectionId);
    }

    private String redirectToYourFinances(long applicationId) {
        // IFS-4848 - we're constructing this URL in a few places - maybe a NavigationUtil?
        return "redirect:" + String.format("%s%d/form/FINANCE", APPLICATION_BASE_URL, applicationId);
    }
}
