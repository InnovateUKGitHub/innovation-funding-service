package org.innovateuk.ifs.application.forms.sections.yourprojectlocation.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.innovateuk.ifs.application.forms.sections.common.viewmodel.CommonYourProjectFinancesViewModel;
import org.innovateuk.ifs.application.forms.sections.common.viewmodel.CommonYourFinancesViewModelPopulator;
import org.innovateuk.ifs.application.forms.sections.yourprojectlocation.form.YourProjectLocationForm;
import org.innovateuk.ifs.application.forms.sections.yourprojectlocation.form.YourProjectLocationFormPopulator;
import org.innovateuk.ifs.application.service.SectionService;
import org.innovateuk.ifs.async.annotations.AsyncMethod;
import org.innovateuk.ifs.async.generation.AsyncAdaptor;
import org.innovateuk.ifs.commons.error.Error;
import org.innovateuk.ifs.commons.error.ValidationMessages;
import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.controller.ValidationHandler;
import org.innovateuk.ifs.finance.resource.ApplicationFinanceResource;
import org.innovateuk.ifs.finance.service.ApplicationFinanceRestService;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.user.resource.ProcessRoleResource;
import org.innovateuk.ifs.user.resource.Role;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.service.OrganisationRestService;
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
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.application.forms.ApplicationFormUtil.APPLICATION_BASE_URL;
import static org.innovateuk.ifs.commons.error.Error.fieldError;

/**
 * The Controller for the "Your project location" page in the Application Form process.
 */
@Controller
@RequestMapping(APPLICATION_BASE_URL + "{applicationId}/form/your-project-location/organisation/{organisationId}/section/{sectionId}")
public class YourProjectLocationController extends AsyncAdaptor {

    private static final String VIEW_PAGE = "application/sections/your-project-location/your-project-location";
    private static final int MINIMUM_POSTCODE_LENGTH = 3;
    private static final int MAXIMUM_POSTCODE_LENGTH = 10;

    private CommonYourFinancesViewModelPopulator commonViewModelPopulator;
    private YourProjectLocationFormPopulator formPopulator;
    private ApplicationFinanceRestService applicationFinanceRestService;
    private SectionService sectionService;
    private UserRestService userRestService;
    private OrganisationRestService organisationRestService;

    public YourProjectLocationController() {
    }

    @Autowired
    YourProjectLocationController(
            CommonYourFinancesViewModelPopulator commonViewModelPopulator,
            YourProjectLocationFormPopulator formPopulator,
            ApplicationFinanceRestService applicationFinanceRestService,
            SectionService sectionService,
            UserRestService userRestService,
            OrganisationRestService organisationRestService) {

        this.commonViewModelPopulator = commonViewModelPopulator;
        this.formPopulator = formPopulator;
        this.applicationFinanceRestService = applicationFinanceRestService;
        this.sectionService = sectionService;
        this.userRestService = userRestService;
        this.organisationRestService = organisationRestService;
    }

    @GetMapping
    @AsyncMethod
    @PreAuthorize("hasAnyAuthority('applicant', 'support', 'innovation_lead', 'ifs_administrator', 'comp_admin', 'project_finance', 'stakeholder', 'external_finance')")
    @SecuredBySpring(value = "VIEW_PROJECT_LOCATION", description = "Applicants, stakeholders and internal users can view the Your project location page")
    public String viewPage(
            @PathVariable("applicationId") long applicationId,
            @PathVariable("organisationId") long organisationId,
            @PathVariable("sectionId") long sectionId,
            UserResource loggedInUser,
            Model model) {

        Future<CommonYourProjectFinancesViewModel> commonViewModelRequest = async(() ->
                getViewModel(applicationId, sectionId, organisationId, loggedInUser.isInternalUser() || loggedInUser.hasRole(Role.EXTERNAL_FINANCE)));

        Future<YourProjectLocationForm> formRequest = async(() ->
                formPopulator.populate(applicationId, organisationId));

        model.addAttribute("model", commonViewModelRequest);
        model.addAttribute("form", formRequest);

        return VIEW_PAGE;
    }

    @PostMapping
    @PreAuthorize("hasAuthority('applicant')")
    @SecuredBySpring(value = "UPDATE_PROJECT_LOCATION", description = "Applicants can update their project location")
    public String update(
            @PathVariable("applicationId") long applicationId,
            @PathVariable("organisationId") long organisationId,
            @ModelAttribute YourProjectLocationForm form) {

        updateLocation(applicationId, organisationId, form);
        return redirectToYourFinances(applicationId);
    }

    @PostMapping("/auto-save")
    @PreAuthorize("hasAuthority('applicant')")
    @SecuredBySpring(value = "UPDATE_PROJECT_LOCATION", description = "Applicants can update their project location")
    public @ResponseBody JsonNode autosave(
            @PathVariable("applicationId") long applicationId,
            @PathVariable("organisationId") long organisationId,
            @ModelAttribute YourProjectLocationForm form) {

        update(applicationId, organisationId, form);
        return new ObjectMapper().createObjectNode();
    }

    @PostMapping(params = "mark-as-complete")
    @PreAuthorize("hasAuthority('applicant')")
    @SecuredBySpring(value = "MARK_PROJECT_LOCATION_AS_COMPLETE", description = "Applicants can mark their project location as complete")
    public String markAsComplete(
            @PathVariable("applicationId") long applicationId,
            @PathVariable("organisationId") long organisationId,
            @PathVariable("sectionId") long sectionId,
            UserResource loggedInUser,
            @Valid @ModelAttribute("form") YourProjectLocationForm form,
            @SuppressWarnings("unused") BindingResult bindingResult,
            ValidationHandler validationHandler,
            Model model) {

        formatLocationInForm(form);

        Supplier<String> failureHandler = () -> {
            CommonYourProjectFinancesViewModel viewModel = getViewModel(applicationId, sectionId, organisationId, false);
            model.addAttribute("model", viewModel);
            model.addAttribute("form", form);
            return VIEW_PAGE;
        };

        Supplier<String> successHandler = () -> {

            updateLocation(applicationId, organisationId, form);

            ProcessRoleResource processRole = userRestService.findProcessRole(loggedInUser.getId(), applicationId).getSuccess();
            ValidationMessages validationMessages = sectionService.markAsComplete(sectionId, applicationId, processRole.getId());
            validationHandler.addAnyErrors(validationMessages);

            return validationHandler.failNowOrSucceedWith(failureHandler, () -> redirectToYourFinances(applicationId));
        };

        return validationHandler.
                addAnyErrors(validateProjectLocation(organisationId, form)).
                failNowOrSucceedWith(failureHandler, successHandler);
    }

    @PostMapping(params = "mark-as-incomplete")
    @PreAuthorize("hasAuthority('applicant')")
    @SecuredBySpring(value = "MARK_PROJECT_LOCATION_AS_INCOMPLETE", description = "Applicants can mark their project location as incomplete")
    public String markAsIncomplete(
            @PathVariable("applicationId") long applicationId,
            @PathVariable("organisationId") long organisationId,
            @PathVariable("sectionId") long sectionId,
            UserResource loggedInUser) {

        ProcessRoleResource processRole = userRestService.findProcessRole(loggedInUser.getId(), applicationId).getSuccess();
        sectionService.markAsInComplete(sectionId, applicationId, processRole.getId());
        return redirectToViewPage(applicationId, organisationId, sectionId);
    }

    private void formatLocationInForm(YourProjectLocationForm form) {
        if (form.getPostcode() != null) {
            form.setPostcode(form.getPostcode().trim().toUpperCase());
        }
        if(form.getTown() != null) {
            String townFixedCase = Stream.of(form.getTown().split(" "))
                    .filter(w -> w.length() > 0)
                    .map(word -> {
                        String wordCased = word.substring(0, 1).toUpperCase();
                        if (word.length() > 1) {
                            wordCased = wordCased + word.substring(1).toLowerCase();
                        }
                        return wordCased;
                    })
                    .collect(Collectors.joining(" "));
            form.setTown(townFixedCase);
        }
    }

    private void updateLocation(long applicationId,
                                long organisationId,
                                YourProjectLocationForm form) {

        formatLocationInForm(form);

        ApplicationFinanceResource finance =
                applicationFinanceRestService.getApplicationFinance(applicationId, organisationId).getSuccess();

        finance.setWorkPostcode(form.getPostcode());
        finance.setInternationalLocation(form.getTown());

        applicationFinanceRestService.update(finance.getId(), finance).getSuccess();
    }

    private List<Error> validateProjectLocation(Long organisationId, YourProjectLocationForm form) {

        OrganisationResource organisation = organisationRestService.getOrganisationById(organisationId).getSuccess();

        if (organisation.isInternational()) {
            String town = form.getTown();
            if (StringUtils.isBlank(town)) {
                return singletonList(fieldError("town", town, "APPLICATION_PROJECT_LOCATION_TOWN_REQUIRED"));
            } else {
                return emptyList();
            }
        } else {
            String postcode = form.getPostcode();
            if (postcode.length() >= MINIMUM_POSTCODE_LENGTH && postcode.length() <= MAXIMUM_POSTCODE_LENGTH) {
                return emptyList();
            }

            return singletonList(fieldError("postcode", postcode, "APPLICATION_PROJECT_LOCATION_REQUIRED"));
        }
    }

    private CommonYourProjectFinancesViewModel getViewModel(long applicationId, long sectionId, long organisationId, boolean internalUser) {
        return commonViewModelPopulator.populate(organisationId, applicationId, sectionId, internalUser);
    }

    private String redirectToViewPage(long applicationId, long organisationId, long sectionId) {
        return "redirect:" + APPLICATION_BASE_URL +
                String.format("%d/form/your-project-location/organisation/%d/section/%d",
                        applicationId,
                        organisationId,
                        sectionId);
    }

    private String redirectToYourFinances(long applicationId) {
        // IFS-4848 - we're constructing this URL in a few places - maybe a NavigationUtil?
        return "redirect:" + String.format("%s%d/form/FINANCE", APPLICATION_BASE_URL, applicationId);
    }
}
