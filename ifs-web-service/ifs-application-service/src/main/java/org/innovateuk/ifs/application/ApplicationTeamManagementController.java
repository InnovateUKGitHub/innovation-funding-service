package org.innovateuk.ifs.application;

import org.innovateuk.ifs.application.form.ApplicantInviteForm;
import org.innovateuk.ifs.application.form.ApplicationTeamUpdateForm;
import org.innovateuk.ifs.application.form.RemoveContributorsForm;
import org.innovateuk.ifs.application.populator.ApplicationTeamManagementModelPopulator;
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
import java.util.List;
import java.util.function.Supplier;

import static java.lang.String.format;
import static java.util.stream.Collectors.toList;
import static org.innovateuk.ifs.controller.ErrorToObjectErrorConverterFactory.asGlobalErrors;
import static org.innovateuk.ifs.controller.ErrorToObjectErrorConverterFactory.fieldErrorsToFieldErrors;

/**
 * This controller will handle all requests that are related to the management of application participants
 */
@Controller
@RequestMapping("/application/{applicationId}/team/update")
@PreAuthorize("hasAuthority('applicant')")
public class ApplicationTeamManagementController {

    private static final String FORM_ATTR_NAME = "form";

    @Autowired
    private InviteRestService inviteRestService;

    @Autowired
    private ApplicationService applicationService;

    @Autowired
    private UserService userService;

    @Autowired
    private ApplicationTeamManagementModelPopulator applicationTeamManagementModelPopulator;

    @GetMapping(params = {"organisation"})
    public String getUpdateOrganisation(Model model,
                                        @PathVariable("applicationId") long applicationId,
                                        @RequestParam(name = "organisation") long organisationId,
                                        @ModelAttribute("loggedInUser") UserResource loggedInUser,
                                        @ModelAttribute(FORM_ATTR_NAME) ApplicationTeamUpdateForm form) {
        ApplicationResource applicationResource = applicationService.getById(applicationId);
        checkUserHasAuthority(applicationResource, organisationId, loggedInUser.getId());

        model.addAttribute("model", applicationTeamManagementModelPopulator.populateModel(applicationId, organisationId,
                loggedInUser.getId()));
        return "application-team/edit-org";
    }

    @GetMapping(params = {"organisationName"})
    public String getUpdateOrganisation(Model model,
                                        @PathVariable("applicationId") long applicationId,
                                        @RequestParam(name = "organisationName") String organisationName,
                                        @ModelAttribute("loggedInUser") UserResource loggedInUser,
                                        @ModelAttribute(FORM_ATTR_NAME) ApplicationTeamUpdateForm form) {
        ApplicationResource applicationResource = applicationService.getById(applicationId);
        checkUserIsLeadApplicant(applicationResource, loggedInUser.getId());

        model.addAttribute("model", applicationTeamManagementModelPopulator.populateModel(applicationId, organisationName,
                loggedInUser.getId()));
        return "application-team/edit-org";
    }

    @PostMapping(params = {"organisation"})
    public String submitUpdateOrganisation(Model model,
                                           @PathVariable("applicationId") Long applicationId,
                                           @RequestParam(name = "organisation") long organisationId,
                                           @ModelAttribute("loggedInUser") UserResource loggedInUser,
                                           @Valid @ModelAttribute(FORM_ATTR_NAME) ApplicationTeamUpdateForm form,
                                           @SuppressWarnings("unused") BindingResult bindingResult,
                                           ValidationHandler validationHandler) {

        ApplicationResource applicationResource = applicationService.getById(applicationId);
        checkUserHasAuthority(applicationResource, organisationId, loggedInUser.getId());

        Supplier<String> failureView = () -> getUpdateOrganisation(model, applicationId, organisationId, loggedInUser, form);

        return validationHandler.failNowOrSucceedWith(failureView, () -> {
            ServiceResult<InviteResultsResource> updateResult = createInvitesByOrganisation(organisationId, form, applicationId);
            return validationHandler.addAnyErrors(updateResult, fieldErrorsToFieldErrors(), asGlobalErrors())
                    .failNowOrSucceedWith(failureView, () -> format("redirect:/application/%s/team", applicationId));
        });
    }

    @PostMapping(params = {"organisationName"})
    public String submitUpdateOrganisation(Model model,
                                           @PathVariable("applicationId") Long applicationId,
                                           @RequestParam(name = "organisationName") String organisationName,
                                           @ModelAttribute("loggedInUser") UserResource loggedInUser,
                                           @Valid @ModelAttribute ApplicationTeamUpdateForm form,
                                           @SuppressWarnings("unused") BindingResult bindingResult,
                                           ValidationHandler validationHandler) {

        ApplicationResource applicationResource = applicationService.getById(applicationId);
        checkUserIsLeadApplicant(applicationResource, loggedInUser.getId());

        Supplier<String> failureView = () -> getUpdateOrganisation(model, applicationId, organisationName, loggedInUser, form);

        return validationHandler.failNowOrSucceedWith(failureView, () -> {
            ServiceResult<InviteResultsResource> updateResult = createInvitesByInviteOrganisation(organisationName, form, applicationId);
            return validationHandler.addAnyErrors(updateResult, fieldErrorsToFieldErrors(), asGlobalErrors())
                    .failNowOrSucceedWith(failureView, () -> format("redirect:/application/%s/team", applicationId));
        });
    }


    @RequestMapping(value = "update/remove/{inviteId}/confirm", method = RequestMethod.GET)
    public String deleteContributorConfirmation(@PathVariable("applicationId") long applicationId,
                                                @PathVariable("inviteId") long inviteId,
                                                Model model) {
        model.addAttribute("currentApplication", applicationService.getById(applicationId));
        model.addAttribute("inviteId", inviteId);
        model.addAttribute("removeContributorForm", new RemoveContributorsForm());
        return "application-team/update-remove-confirm";
    }

    @RequestMapping(value = "update/remove", method = RequestMethod.POST)
    public String deleteContributor(@PathVariable("applicationId") long applicationId,
                                    @Valid @ModelAttribute RemoveContributorsForm removeContributorsForm) {
        applicationService.removeCollaborator(removeContributorsForm.getApplicationInviteId()).getSuccessObjectOrThrowException();
        return format("redirect:/application/%s/team", applicationId);
    }

    private ServiceResult<InviteResultsResource> createInvitesByOrganisation(long organisationId, ApplicationTeamUpdateForm form, long applicationId) {
        return inviteRestService.createInvitesByOrganisation(organisationId, createInvites(form, applicationId)).toServiceResult();
    }

    private ServiceResult<InviteResultsResource> createInvitesByInviteOrganisation(String organisationName, ApplicationTeamUpdateForm form, long applicationId) {
        return inviteRestService.createInvitesByInviteOrganisation(organisationName, createInvites(form, applicationId)).toServiceResult();
    }

    @PostMapping(params = {"addApplicant"})
    public String addApplicant(Model model,
                               @PathVariable("applicationId") long applicationId,
                               @RequestParam(name = "organisation", required = false) long organisationId,
                               @ModelAttribute("loggedInUser") UserResource loggedInUser,
                               @ModelAttribute(FORM_ATTR_NAME) ApplicationTeamUpdateForm form) {
        ApplicationResource applicationResource = applicationService.getById(applicationId);
        checkUserHasAuthority(applicationResource, organisationId, loggedInUser.getId());

        form.getApplicants().add(new ApplicantInviteForm());
        return getUpdateOrganisation(model, applicationId, organisationId, loggedInUser, form);
    }

    @PostMapping(params = {"removeApplicant"})
    public String removeApplicant(Model model,
                                  @PathVariable("applicationId") long applicationId,
                                  @RequestParam(name = "organisation", required = false) long organisationId,
                                  @ModelAttribute("loggedInUser") UserResource loggedInUser,
                                  @ModelAttribute(FORM_ATTR_NAME) ApplicationTeamUpdateForm form,
                                  @RequestParam(name = "removeApplicant") Integer position) {
        ApplicationResource applicationResource = applicationService.getById(applicationId);
        checkUserHasAuthority(applicationResource, organisationId, loggedInUser.getId());

        form.getApplicants().remove(position.intValue());
        return getUpdateOrganisation(model, applicationId, organisationId, loggedInUser, form);
    }

    private void checkUserHasAuthority(ApplicationResource applicationResource, long organisationId, long loggedInUserId) {
        // TODO throw exception if user doesn't have authority
    }

    private void checkUserIsLeadApplicant(ApplicationResource applicationResource, long loggedInUserId) {
        if (loggedInUserId != getLeadApplicantId(applicationResource)) {
            throw new ForbiddenActionException("User must be Lead Applicant");
        }
    }

    private long getLeadApplicantId(ApplicationResource applicationResource) {
        return userService.getLeadApplicantProcessRoleOrNull(applicationResource).getUser();
    }

    private List<ApplicationInviteResource> createInvites(ApplicationTeamUpdateForm applicationTeamUpdateForm,
                                                          long applicationId) {
        return applicationTeamUpdateForm.getApplicants().stream()
                .map(applicantInviteForm -> createInvite(applicantInviteForm, applicationId)).collect(toList());
    }

    private ApplicationInviteResource createInvite(ApplicantInviteForm applicantInviteForm, long applicationId) {
        return new ApplicationInviteResource(applicantInviteForm.getName(), applicantInviteForm.getEmail(), applicationId);
    }
}
