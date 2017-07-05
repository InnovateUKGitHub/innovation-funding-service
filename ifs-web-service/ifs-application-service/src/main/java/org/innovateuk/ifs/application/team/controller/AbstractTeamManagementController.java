package org.innovateuk.ifs.application.team.controller;

import org.innovateuk.ifs.application.team.form.ApplicantInviteForm;
import org.innovateuk.ifs.application.team.form.ApplicationTeamUpdateForm;
import org.innovateuk.ifs.application.team.service.AbstractTeamManagementService;
import org.innovateuk.ifs.application.team.viewmodel.ApplicationTeamManagementViewModel;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.controller.ValidationHandler;
import org.innovateuk.ifs.invite.resource.InviteResultsResource;
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

public abstract class AbstractTeamManagementController<TeamManagementServiceType extends AbstractTeamManagementService> {
    protected static final String FORM_ATTR_NAME = "form";

    @Autowired
    private TeamManagementServiceType teamManagementService;

    @GetMapping
    public String getUpdateOrganisation(Model model,
                                        @PathVariable("applicationId") long applicationId,
                                        @PathVariable("organisationId") long organisationId,
                                        UserResource loggedInUser,
                                        @ModelAttribute(name = FORM_ATTR_NAME, binding = false) ApplicationTeamUpdateForm form) {
        return teamManagementService.validateOrganisationAndApplicationIds(applicationId, organisationId, () -> {
            ApplicationTeamManagementViewModel viewModel = teamManagementService.createViewModel(applicationId, organisationId, loggedInUser);
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
        return teamManagementService.validateOrganisationAndApplicationIds(applicationId, inviteOrganisationId, () -> {
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
        return teamManagementService.validateOrganisationAndApplicationIds(applicationId, inviteOrganisationId, () -> {
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
        return teamManagementService.validateOrganisationAndApplicationIds(applicationId, organisationId, () -> {
            Supplier<String> failureView = () -> getUpdateOrganisation(model, applicationId, organisationId, loggedInUser, form);

            return validationHandler.failNowOrSucceedWith(failureView, () -> {
                ServiceResult<InviteResultsResource> updateResult = teamManagementService.executeStagedInvite(applicationId, organisationId, form);

                form.setStagedInvite(null);
                return validationHandler.addAnyErrors(updateResult, fieldErrorsToFieldErrors(), asGlobalErrors())
                        .failNowOrSucceedWith(failureView, () -> getUpdateOrganisation(model, applicationId, organisationId, loggedInUser, form));
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

        return teamManagementService.validateOrganisationAndApplicationIds(applicationId, organisationId, () -> {
            Supplier<String> failureView = () -> getUpdateOrganisation(model, applicationId, organisationId, loggedInUser, form);

            return validationHandler.failNowOrSucceedWith(failureView, () -> {
                ServiceResult<Void> updateResult = teamManagementService.removeInvite(applicantId);
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
        return teamManagementService.validateOrganisationAndApplicationIds(applicationId, organisationId, () -> {
            model.addAttribute("model", teamManagementService.createViewModel(applicationId, organisationId, loggedInUser));

            return "application-team/delete-org";
        });
    }

    @PostMapping(params = {"deleteOrganisation"})
    public String deleteOrganisation(Model model,
                                     @PathVariable("applicationId") Long applicationId,
                                     @PathVariable("organisationId") long organisationId,
                                     UserResource loggedInUser,
                                     @Valid @ModelAttribute(FORM_ATTR_NAME) ApplicationTeamUpdateForm form) {

        return teamManagementService.validateOrganisationAndApplicationIds(applicationId, organisationId, () -> {
            List<Long> existingApplicantIds = teamManagementService.getInviteIds(applicationId, organisationId);

            return processAnyFailuresOrSucceed(simpleMap(existingApplicantIds, teamManagementService::removeInvite))
                    .handleSuccessOrFailure(
                            failure -> getUpdateOrganisation(model, applicationId, organisationId, loggedInUser, form),
                            success -> format("redirect:/application/%s/team", applicationId)
                    );
        });
    }


}
