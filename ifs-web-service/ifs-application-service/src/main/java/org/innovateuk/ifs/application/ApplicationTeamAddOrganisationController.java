package org.innovateuk.ifs.application;

import org.innovateuk.ifs.application.form.ApplicantInviteForm;
import org.innovateuk.ifs.application.form.ApplicationTeamAddOrganisationForm;
import org.innovateuk.ifs.application.populator.ApplicationTeamAddOrganisationModelPopulator;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.service.ApplicationService;
import org.innovateuk.ifs.commons.error.exception.ForbiddenActionException;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.controller.ValidationHandler;
import org.innovateuk.ifs.invite.resource.ApplicationInviteResource;
import org.innovateuk.ifs.invite.resource.InviteResultsResource;
import org.innovateuk.ifs.invite.service.InviteRestService;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

import static java.lang.String.format;
import static java.util.stream.Collectors.toList;
import static org.innovateuk.ifs.controller.ErrorToObjectErrorConverterFactory.asGlobalErrors;
import static org.innovateuk.ifs.controller.ErrorToObjectErrorConverterFactory.fieldErrorsToFieldErrors;
import static org.innovateuk.ifs.util.CollectionFunctions.forEachWithIndex;

/**
 * This controller will handle all requests that are related to adding a new partner organisation to the application team.
 */
@Controller
@RequestMapping("/application/{applicationId}/team")
@PreAuthorize("hasAuthority('applicant')")
public class ApplicationTeamAddOrganisationController {

    private static final String FORM_ATTR_NAME = "form";

    @Autowired
    private ApplicationService applicationService;

    @Autowired
    private InviteRestService inviteRestService;

    @Autowired
    private UserService userService;

    @Autowired
    private ApplicationTeamAddOrganisationModelPopulator applicationTeamAddOrganisationModelPopulator;

    @GetMapping("/addOrganisation")
    public String getAddOrganisation(Model model,
                                     @PathVariable("applicationId") long applicationId,
                                     @ModelAttribute("loggedInUser") UserResource loggedInUser,
                                     @ModelAttribute(FORM_ATTR_NAME) ApplicationTeamAddOrganisationForm form) {
        ApplicationResource applicationResource = applicationService.getById(applicationId);
        checkUserIsLeadApplicant(applicationResource, loggedInUser.getId());

        if (form.getApplicants().isEmpty()) {
            form.getApplicants().add(new ApplicantInviteForm());
        }

        return doViewAddOrganisation(model, applicationResource);
    }

    @PostMapping("/addOrganisation")
    public String submitAddOrganisation(Model model,
                                        @PathVariable("applicationId") long applicationId,
                                        @ModelAttribute("loggedInUser") UserResource loggedInUser,
                                        @Valid @ModelAttribute(FORM_ATTR_NAME) ApplicationTeamAddOrganisationForm form,
                                        @SuppressWarnings("unused") BindingResult bindingResult,
                                        ValidationHandler validationHandler) {
        ApplicationResource applicationResource = applicationService.getById(applicationId);
        checkUserIsLeadApplicant(applicationResource, loggedInUser.getId());

        validateUniqueEmails(form, bindingResult);

        Supplier<String> failureView = () -> getAddOrganisation(model, applicationId, loggedInUser, form);

        return validationHandler.failNowOrSucceedWith(failureView, () -> {
            ServiceResult<InviteResultsResource> updateResult = inviteRestService.createInvitesByInviteOrganisation(
                    form.getOrganisationName(), createInvites(form, applicationId)).toServiceResult();
            return validationHandler.addAnyErrors(updateResult, fieldErrorsToFieldErrors(), asGlobalErrors())
                    .failNowOrSucceedWith(failureView, () -> format("redirect:/application/%s/team", applicationId));
        });
    }

    @PostMapping(value = "/addOrganisation", params = {"addApplicant"})
    public String addApplicant(Model model,
                               @PathVariable("applicationId") long applicationId,
                               @ModelAttribute("loggedInUser") UserResource loggedInUser,
                               @ModelAttribute(FORM_ATTR_NAME) ApplicationTeamAddOrganisationForm form) {
        ApplicationResource applicationResource = applicationService.getById(applicationId);
        checkUserIsLeadApplicant(applicationResource, loggedInUser.getId());
        form.getApplicants().add(new ApplicantInviteForm());
        return doViewAddOrganisation(model, applicationResource);
    }

    @PostMapping(value = "/addOrganisation", params = {"removeApplicant"})
    public String removeApplicant(Model model,
                                  @PathVariable("applicationId") long applicationId,
                                  @ModelAttribute("loggedInUser") UserResource loggedInUser,
                                  @ModelAttribute(FORM_ATTR_NAME) ApplicationTeamAddOrganisationForm form,
                                  @RequestParam(name = "removeApplicant") Integer position) {
        ApplicationResource applicationResource = applicationService.getById(applicationId);
        checkUserIsLeadApplicant(applicationResource, loggedInUser.getId());
        form.getApplicants().remove(position.intValue());
        return doViewAddOrganisation(model, applicationResource);
    }

    private void checkUserIsLeadApplicant(ApplicationResource applicationResource, long loggedInUserId) {
        if (loggedInUserId != getLeadApplicantId(applicationResource)) {
            throw new ForbiddenActionException("User must be Lead Applicant");
        }
    }

    private long getLeadApplicantId(ApplicationResource applicationResource) {
        return userService.getLeadApplicantProcessRoleOrNull(applicationResource).getUser();
    }

    private String doViewAddOrganisation(Model model, ApplicationResource applicationResource) {
        model.addAttribute("model", applicationTeamAddOrganisationModelPopulator.populateModel(applicationResource));
        return "application-team/add-organisation";
    }

    private List<ApplicationInviteResource> createInvites(ApplicationTeamAddOrganisationForm applicationTeamAddOrganisationForm,
                                                          long applicationId) {
        return applicationTeamAddOrganisationForm.getApplicants().stream()
                .map(applicantInviteForm -> createInvite(applicantInviteForm, applicationId)).collect(toList());
    }

    private ApplicationInviteResource createInvite(ApplicantInviteForm applicantInviteForm, long applicationId) {
        return new ApplicationInviteResource(applicantInviteForm.getName(), applicantInviteForm.getEmail(), applicationId);
    }

    private void validateUniqueEmails(ApplicationTeamAddOrganisationForm form, BindingResult bindingResult) {
        Set<String> emails = new HashSet<>();
        forEachWithIndex(form.getApplicants(), (index, applicantInviteForm) -> {
            if (!emails.add(applicantInviteForm.getEmail())) {
                bindingResult.rejectValue(format("applicants[%s].email", index), "validation.applicationteamaddorganisationform.email.notUnique");
            }
        });
    }
}