package org.innovateuk.ifs.application.team.controller;

import org.innovateuk.ifs.application.service.ApplicationService;
import org.innovateuk.ifs.application.team.form.ApplicantInviteForm;
import org.innovateuk.ifs.application.team.form.ApplicationTeamUpdateForm;
import org.innovateuk.ifs.application.team.populator.ApplicationTeamManagementModelPopulator;
import org.innovateuk.ifs.application.team.viewmodel.ApplicationTeamManagementViewModel;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.controller.ValidationHandler;
import org.innovateuk.ifs.invite.resource.ApplicationInviteResource;
import org.innovateuk.ifs.invite.resource.InviteResultsResource;
import org.innovateuk.ifs.invite.service.InviteOrganisationRestService;
import org.innovateuk.ifs.invite.service.InviteRestService;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.function.Supplier;

import static java.lang.String.format;
import static org.innovateuk.ifs.commons.service.ServiceResult.processAnyFailuresOrSucceed;
import static org.innovateuk.ifs.controller.ErrorToObjectErrorConverterFactory.asGlobalErrors;
import static org.innovateuk.ifs.controller.ErrorToObjectErrorConverterFactory.fieldErrorsToFieldErrors;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;

/**
 * This controller will handle all requests that are related to the management of application participants
 */

public abstract class AbstractApplicationTeamManagementController {
    protected static final String FORM_ATTR_NAME = "form";

    @Autowired
    protected InviteRestService inviteRestService;

    @Autowired
    protected ApplicationService applicationService;

    @Autowired
    protected ApplicationTeamManagementModelPopulator applicationTeamManagementModelPopulator;

    @Autowired
    protected InviteOrganisationRestService inviteOrganisationRestService;

    protected abstract String validateOrganisationAndApplicationIds(Long applicationId,
                                                                    Long organisationId,
                                                                    Supplier<String> supplier);

    protected abstract ApplicationTeamManagementViewModel createViewModel(long applicationId,
                                                                          long organisationId,
                                                                          UserResource loggedInUser);

    protected abstract ServiceResult<InviteResultsResource> executeStagedInvite(long applicationId,
                                                                                long organisationId,
                                                                                ApplicationTeamUpdateForm form);

    protected abstract List<Long> getInviteIds(long applicationId, long organisationId);


    @GetMapping
    public String getUpdateOrganisation(Model model,
                                        @PathVariable("applicationId") long applicationId,
                                        @PathVariable("organisationId") long organisationId,
                                        UserResource loggedInUser,
                                        @ModelAttribute(name = FORM_ATTR_NAME, binding = false) ApplicationTeamUpdateForm form) {
        return validateOrganisationAndApplicationIds(applicationId, organisationId, () -> {
            ApplicationTeamManagementViewModel viewModel = createViewModel(applicationId, organisationId, loggedInUser);
            model.addAttribute("model", viewModel);
            return "application-team/edit-org";
        });
    }

    @PostMapping(params = {"addStagedInvite"})
    public String addStagedInvite(Model model,
                                  @PathVariable("applicationId") long applicationId,
                                  @PathVariable("organisationId") long inviteOrganisationId,
                                  UserResource loggedInUser,
                                  @ModelAttribute(FORM_ATTR_NAME) ApplicationTeamUpdateForm form) {
        return validateOrganisationAndApplicationIds(applicationId, inviteOrganisationId, () -> {
            form.setStagedInvite(new ApplicantInviteForm());
            return getUpdateOrganisation(model, applicationId, inviteOrganisationId, loggedInUser, form);
        });
    }

    @PostMapping(params = {"removeStagedInvite"})
    public String removeStagedInvite(Model model,
                                     @PathVariable("applicationId") long applicationId,
                                     @RequestParam("organisationId") long inviteOrganisationId,
                                     UserResource loggedInUser,
                                     @ModelAttribute(FORM_ATTR_NAME) ApplicationTeamUpdateForm form,
                                     @RequestParam(name = "removeApplicant") Integer position) {
        return validateOrganisationAndApplicationIds(applicationId, inviteOrganisationId, () -> {
            form.setStagedInvite(null);
            return getUpdateOrganisation(model, applicationId, inviteOrganisationId, loggedInUser, form);
        });
    }

    @PostMapping(params = {"executeStagedInvite"})
    public String inviteApplicant(Model model,
                                  @PathVariable("applicationId") Long applicationId,
                                  @PathVariable("organisationId") long organisationId,
                                  UserResource loggedInUser,
                                  @Valid @ModelAttribute(FORM_ATTR_NAME) ApplicationTeamUpdateForm form,
                                  @SuppressWarnings("unused") BindingResult bindingResult,
                                  ValidationHandler validationHandler) {
        return validateOrganisationAndApplicationIds(applicationId, organisationId, () -> {
            Supplier<String> failureView = () -> getUpdateOrganisation(model, applicationId, organisationId, loggedInUser, form);

            return validationHandler.failNowOrSucceedWith(failureView, () -> {
                ServiceResult<InviteResultsResource> updateResult = executeStagedInvite(applicationId, organisationId, form);

                return validationHandler.addAnyErrors(updateResult, fieldErrorsToFieldErrors(), asGlobalErrors())
                        .failNowOrSucceedWith(failureView, () -> format("redirect:/application/%s/team", applicationId));
            });
        });
    }

    @PostMapping(params = {"removeInvite"})
    public String removeApplicant(Model model,
                                  @PathVariable("applicationId") long applicationId,
                                  @PathVariable("organisationId") long organisationId,
                                  @RequestParam("removeInvite") long applicantId,
                                  UserResource loggedInUser,
                                  @ModelAttribute(FORM_ATTR_NAME) ApplicationTeamUpdateForm form,
                                  ValidationHandler validationHandler) {

        return validateOrganisationAndApplicationIds(applicationId, organisationId, () -> {
            Supplier<String> failureView = () -> getUpdateOrganisation(model, applicationId, organisationId, loggedInUser, form);

            return validationHandler.failNowOrSucceedWith(failureView, () -> {
                ServiceResult<Void> updateResult = removeInvite(applicantId);
                validationHandler.addAnyErrors(updateResult, fieldErrorsToFieldErrors(), asGlobalErrors());

                return getUpdateOrganisation(model, applicationId, organisationId, loggedInUser, form);
            });
        }) ;
    }

    @GetMapping(params = {"deleteOrganisation"})
    public String confirmDeleteInviteOrganisation(Model model,
                                                  @PathVariable("applicationId") long applicationId,
                                                  @RequestParam("organisationId") long organisationId,
                                                  UserResource loggedInUser) {
        return validateOrganisationAndApplicationIds(applicationId, organisationId, () -> {
            model.addAttribute("model", createViewModel(applicationId, organisationId, loggedInUser));

            return "application-team/delete-org";
        });
    }

    @PostMapping(params = {"deleteOrganisation"})
    public String deleteOrganisation(Model model,
                                     @PathVariable("applicationId") Long applicationId,
                                     @PathVariable("organisationId") long organisationId,
                                     UserResource loggedInUser,
                                     @Valid @ModelAttribute(FORM_ATTR_NAME) ApplicationTeamUpdateForm form) {

        return validateOrganisationAndApplicationIds(applicationId, organisationId, () -> {
            List<Long> existingApplicantIds = getInviteIds(applicationId, organisationId);

            return processAnyFailuresOrSucceed(simpleMap(existingApplicantIds, applicationService::removeCollaborator))
                    .handleSuccessOrFailure(
                            failure -> getUpdateOrganisation(model, applicationId, organisationId, loggedInUser, form),
                            success -> format("redirect:/application/%s/team", applicationId)
                    );
        });
    }

    protected ApplicationInviteResource createInvite(ApplicationTeamUpdateForm applicationTeamUpdateForm,
                                                          long applicationId,
                                                          Long inviteOrganisationId) {

        return createInvite(applicationTeamUpdateForm.getStagedInvite(), applicationId, inviteOrganisationId);
    }

    protected ApplicationInviteResource createInvite(ApplicantInviteForm applicantInviteForm,
                                                   long applicationId,
                                                   Long inviteOrganisationId) {
        ApplicationInviteResource applicationInviteResource = new ApplicationInviteResource(
                applicantInviteForm.getName(),
                applicantInviteForm.getEmail(),
                applicationId
        );
        applicationInviteResource.setInviteOrganisation(inviteOrganisationId);

        return applicationInviteResource;
    }

    private ServiceResult<Void> removeInvite(long applicantInviteId) {
        return applicationService.removeCollaborator(applicantInviteId);
    }
}
