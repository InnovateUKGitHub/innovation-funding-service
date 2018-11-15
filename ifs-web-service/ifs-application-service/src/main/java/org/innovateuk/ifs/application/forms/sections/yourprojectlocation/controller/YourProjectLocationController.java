package org.innovateuk.ifs.application.forms.sections.yourprojectlocation.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.innovateuk.ifs.application.forms.sections.yourprojectlocation.form.YourProjectLocationForm;
import org.innovateuk.ifs.application.forms.sections.yourprojectlocation.form.YourProjectLocationFormPopulator;
import org.innovateuk.ifs.application.forms.sections.yourprojectlocation.viewmodel.YourProjectLocationViewModel;
import org.innovateuk.ifs.application.forms.sections.yourprojectlocation.viewmodel.YourProjectLocationViewModelPopulator;
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
import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.application.forms.ApplicationFormUtil.APPLICATION_BASE_URL;
import static org.innovateuk.ifs.commons.error.Error.fieldError;

/**
 * The Controller for the "Your project location" page in the Application Form process.
 */
@Controller
@RequestMapping(APPLICATION_BASE_URL + "{applicationId}/form/your-project-location/{sectionId}")
@PreAuthorize("hasAuthority('applicant')")
@SecuredBySpring(value = "PROJECT_LOCATION_APPLICANT",
        description = "Applicants can all fill out the Project Location section of the application.")
public class YourProjectLocationController extends AsyncAdaptor {

    private static final String VIEW_PAGE = "application/sections/your-project-location/your-project-location";
    private static final int MINIMUM_POSTCODE_LENGTH = 3;
    private static final int MAXIMUM_POSTCODE_LENGTH = 10;

    private YourProjectLocationViewModelPopulator viewModelPopulator;
    private YourProjectLocationFormPopulator formPopulator;
    private ApplicationFinanceRestService applicationFinanceRestService;
    private SectionService sectionService;
    private UserRestService userRestService;

    YourProjectLocationController(
            YourProjectLocationViewModelPopulator viewModelPopulator,
            YourProjectLocationFormPopulator formPopulator,
            ApplicationFinanceRestService applicationFinanceRestService,
            SectionService sectionService,
            UserRestService userRestService) {

        this.viewModelPopulator = viewModelPopulator;
        this.formPopulator = formPopulator;
        this.applicationFinanceRestService = applicationFinanceRestService;
        this.sectionService = sectionService;
        this.userRestService = userRestService;
    }

    @GetMapping
    @AsyncMethod
    public String view(
            @PathVariable("applicationId") long applicationId,
            @PathVariable("sectionId") long sectionId,
            UserResource loggedInUser,
            Model model) {

        long userId = loggedInUser.getId();

        Future<YourProjectLocationViewModel> viewModelRequest = async(() ->
                getViewModel(applicationId, sectionId, userId));

        Future<YourProjectLocationForm> formRequest = async(() -> {
            ProcessRoleResource processRole = userRestService.findProcessRole(userId, applicationId).getSuccess();
            return formPopulator.populate(applicationId, processRole.getOrganisationId());
        });

        model.addAttribute("model", viewModelRequest);
        model.addAttribute("form", formRequest);

        return VIEW_PAGE;
    }

    @PostMapping
    public String update(
            @PathVariable("applicationId") long applicationId,
            UserResource loggedInUser,
            @ModelAttribute YourProjectLocationForm form) {

        ProcessRoleResource processRole = userRestService.findProcessRole(loggedInUser.getId(), applicationId).getSuccess();
        updatePostcode(applicationId, form, processRole);
        return redirectToYourFinances(applicationId);
    }

    @PostMapping("/auto-save")
    public @ResponseBody
    JsonNode autosave(
            @PathVariable("applicationId") long applicationId,
            UserResource loggedInUser,
            @ModelAttribute YourProjectLocationForm form) {

        update(applicationId, loggedInUser, form);
        return new ObjectMapper().createObjectNode();
    }

    @PostMapping(params = "mark-as-complete")
    public String markAsComplete(
            @PathVariable("applicationId") long applicationId,
            @PathVariable("sectionId") long sectionId,
            UserResource loggedInUser,
            @Valid @ModelAttribute("form") YourProjectLocationForm form,
            @SuppressWarnings("unused") BindingResult bindingResult,
            ValidationHandler validationHandler,
            Model model) {

        Supplier<String> failureHandler = () -> {
            YourProjectLocationViewModel viewModel = getViewModel(applicationId, sectionId, loggedInUser.getId());
            model.addAttribute("model", viewModel);
            model.addAttribute("form", form);
            return VIEW_PAGE;
        };

        Supplier<String> successHandler = () -> {
            ProcessRoleResource processRole = userRestService.findProcessRole(loggedInUser.getId(), applicationId).getSuccess();
            updatePostcode(applicationId, form, processRole);
            List<ValidationMessages> validationMessages = sectionService.markAsComplete(sectionId, applicationId, processRole.getId());
            validationMessages.forEach(validationHandler::addAnyErrors);
            return validationHandler.failNowOrSucceedWith(failureHandler, () -> redirectToViewPage(applicationId, sectionId));
        };

        return validationHandler.
                addAnyErrors(validateProjectLocation(form.getPostcode())).
                failNowOrSucceedWith(failureHandler, successHandler);
    }

    @PostMapping(params = "mark-as-incomplete")
    public String markAsIncomplete(
            @PathVariable("applicationId") long applicationId,
            @PathVariable("sectionId") long sectionId,
            UserResource loggedInUser) {

        ProcessRoleResource processRole = userRestService.findProcessRole(loggedInUser.getId(), applicationId).getSuccess();
        sectionService.markAsInComplete(sectionId, applicationId, processRole.getId());
        return redirectToViewPage(applicationId, sectionId);
    }

    private void updatePostcode(long applicationId,
                                YourProjectLocationForm form,
                                ProcessRoleResource processRole) {

        ApplicationFinanceResource finance =
                applicationFinanceRestService.getFinanceDetails(applicationId, processRole.getOrganisationId()).getSuccess();

        finance.setWorkPostcode(form.getPostcode());

        applicationFinanceRestService.update(finance.getId(), finance).getSuccess();
    }

    private List<Error> validateProjectLocation(String postcode) {
        if (postcode.length() >= MINIMUM_POSTCODE_LENGTH && postcode.length() <= MAXIMUM_POSTCODE_LENGTH) {
            return emptyList();
        }

        return singletonList(fieldError("postcode", postcode, "APPLICATION_PROJECT_LOCATION_REQUIRED"));
    }

    private YourProjectLocationViewModel getViewModel(long applicationId, long sectionId, long userId) {
        return viewModelPopulator.populate(userId, applicationId, sectionId);
    }

    private String redirectToViewPage(long applicationId, long sectionId) {
        return "redirect:" + APPLICATION_BASE_URL + String.format("%d/form/your-project-location/%d", applicationId, sectionId);
    }

    private String redirectToYourFinances(long applicationId) {
        // TODO DW - we're constructing this URL in a few places - maybe a NavigationUtil?
        return "redirect:" + String.format("%s%d/form/FINANCE", APPLICATION_BASE_URL, applicationId);
    }
}
