package org.innovateuk.ifs.application.team.controller;

import org.innovateuk.ifs.application.team.form.ApplicantInviteForm;
import org.innovateuk.ifs.application.team.form.ApplicationTeamUpdateForm;
import org.innovateuk.ifs.application.team.service.AbstractTeamManagementService;
import org.innovateuk.ifs.application.team.viewmodel.ApplicationTeamManagementViewModel;
import org.innovateuk.ifs.commons.error.exception.ObjectNotFoundException;
import org.innovateuk.ifs.commons.security.NotSecured;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.controller.ValidationHandler;
import org.innovateuk.ifs.invite.resource.InviteResultsResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Collections;
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

    protected abstract String getMappingFormatString(long applicationId, long organisationId);

    @NotSecured("Not currently secured")
    @GetMapping
    public String getUpdateOrganisation(Model model,
                                        @PathVariable("applicationId") long applicationId,
                                        @PathVariable("organisationId") long organisationId,
                                        UserResource loggedInUser,
                                        @ModelAttribute(name = FORM_ATTR_NAME, binding = false) ApplicationTeamUpdateForm form) {
        return validateOrganisationAndApplicationIds(applicationId, organisationId, () -> {
            ApplicationTeamManagementViewModel viewModel = teamManagementService.createViewModel(applicationId, organisationId, loggedInUser);
            model.addAttribute("model", viewModel);
            return "application-team/edit-org";
        });
    }

    @NotSecured("Not currently secured")
    @PostMapping(params = {"addStagedInvite"})
    public String addStagedInvite(Model model,
                                  @PathVariable("applicationId") long applicationId,
                                  @PathVariable("organisationId") long organisationId,
                                  UserResource loggedInUser,
                                  @ModelAttribute(FORM_ATTR_NAME) ApplicationTeamUpdateForm form) {
        return validateOrganisationAndApplicationIds(applicationId, organisationId, () -> {
            form.setStagedInvite(new ApplicantInviteForm());
            return getUpdateOrganisation(model, applicationId, organisationId, loggedInUser, form);
        });
    }

    @NotSecured("Not currently secured")
    @PostMapping(params = {"removeStagedInvite"})
    public String removeStagedInvite(Model model,
                                     @PathVariable("applicationId") long applicationId,
                                     @PathVariable("organisationId") long organisationId,
                                     UserResource loggedInUser,
                                     @ModelAttribute(FORM_ATTR_NAME) ApplicationTeamUpdateForm form) {
        return validateOrganisationAndApplicationIds(applicationId, organisationId, () ->
            redirectToOrganisationTeamPage(applicationId, organisationId));
    }

    @NotSecured("Not currently secured")
    @PostMapping(params = {"executeStagedInvite"})
    public String inviteApplicant(Model model,
                                  @PathVariable("applicationId") long applicationId,
                                  @PathVariable("organisationId") long organisationId,
                                  UserResource loggedInUser,
                                  @Valid @ModelAttribute(FORM_ATTR_NAME) ApplicationTeamUpdateForm form,
                                  @SuppressWarnings("unused") BindingResult bindingResult,
                                  ValidationHandler validationHandler) {
        return validateOrganisationAndApplicationIds(applicationId, organisationId, () -> {
            Supplier<String> failureView = () -> getUpdateOrganisation(model, applicationId, organisationId, loggedInUser, form);

            return validationHandler.failNowOrSucceedWith(failureView, () -> {
                ServiceResult<InviteResultsResource> updateResult = teamManagementService.executeStagedInvite(applicationId, organisationId, form);

                return validationHandler.addAnyErrors(updateResult, fieldErrorsToFieldErrors(), asGlobalErrors())
                        .failNowOrSucceedWith(failureView, () -> redirectToOrganisationTeamPage(applicationId, organisationId));
            });
        });
    }

    @NotSecured("Not currently secured")
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
                ServiceResult<Void> updateResult = teamManagementService.removeInvite(applicantId);
                validationHandler.addAnyErrors(updateResult, fieldErrorsToFieldErrors(), asGlobalErrors());

                return getUpdateOrganisation(model, applicationId, organisationId, loggedInUser, form);
            });
        }) ;
    }

    @NotSecured("Not currently secured")
    @GetMapping(params = {"deleteOrganisation"})
    public String confirmDeleteInviteOrganisation(Model model,
                                                  @PathVariable("applicationId") long applicationId,
                                                  @RequestParam("organisationId") long organisationId,
                                                  UserResource loggedInUser) {
        return validateOrganisationAndApplicationIds(applicationId, organisationId, () -> {
            model.addAttribute("model", teamManagementService.createViewModel(applicationId, organisationId, loggedInUser));

            return "application-team/delete-org";
        });
    }

    @NotSecured("Not currently secured")
    @PostMapping(params = {"deleteOrganisation"})
    public String deleteOrganisation(Model model,
                                     @PathVariable("applicationId") long applicationId,
                                     @PathVariable("organisationId") long organisationId,
                                     UserResource loggedInUser,
                                     @Valid @ModelAttribute(FORM_ATTR_NAME) ApplicationTeamUpdateForm form) {

        return validateOrganisationAndApplicationIds(applicationId, organisationId, () -> {
            List<Long> existingApplicantIds = teamManagementService.getInviteIds(applicationId, organisationId);

            return processAnyFailuresOrSucceed(simpleMap(existingApplicantIds, teamManagementService::removeInvite))
                    .handleSuccessOrFailure(
                            failure -> getUpdateOrganisation(model, applicationId, organisationId, loggedInUser, form),
                            success -> redirectToApplicationTeamPage(applicationId)
                    );
        });
    }

    protected String redirectToApplicationTeamPage(long applicationId) {
        return format("redirect:/application/%s/team", applicationId);
    }

    protected String redirectToOrganisationTeamPage(long applicationId, long organisationId) {
        return format("redirect:%s", getMappingFormatString(applicationId, organisationId));
    }

    protected String validateOrganisationAndApplicationIds(Long applicationId, Long organisationId, Supplier<String> supplier) {
        if(teamManagementService.applicationAndOrganisationIdCombinationIsValid(applicationId, organisationId)) {
            return supplier.get();
        }
        throw new ObjectNotFoundException("Organisation invite id not found in application id provided.", Collections.emptyList());
    }
}
