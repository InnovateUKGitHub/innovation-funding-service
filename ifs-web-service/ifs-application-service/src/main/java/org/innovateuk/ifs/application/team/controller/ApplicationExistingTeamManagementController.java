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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static java.lang.String.format;
import static org.innovateuk.ifs.commons.service.ServiceResult.processAnyFailuresOrSucceed;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.controller.ErrorToObjectErrorConverterFactory.asGlobalErrors;
import static org.innovateuk.ifs.controller.ErrorToObjectErrorConverterFactory.fieldErrorsToFieldErrors;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;

/**
 * This controller will handle all requests that are related to the management of application participants
 */
@Controller
@RequestMapping("/application/{applicationId}/team/update")
@PreAuthorize("hasAuthority('applicant')")
public class ApplicationExistingTeamManagementController extends AbstractApplicationTeamManagementController {

    private static final String FORM_ATTR_NAME = "form";

    @Autowired
    private InviteRestService inviteRestService;

    @Autowired
    private InviteOrganisationRestService inviteOrganisationRestService;

    @Autowired
    private ApplicationService applicationService;

    @Autowired
    private ApplicationTeamManagementModelPopulator applicationTeamManagementModelPopulator;

    @GetMapping(params = {"organisation"})
    public String getUpdateOrganisation(Model model,
                                        @PathVariable("applicationId") long applicationId,
                                        @RequestParam(name = "organisation") long organisationId,
                                        UserResource loggedInUser,
                                        @ModelAttribute(name = FORM_ATTR_NAME, binding = false) ApplicationTeamUpdateForm form) {
        return validateOrganisationAndApplicationIds(applicationId, organisationId, () -> {
            ApplicationTeamManagementViewModel viewModel = applicationTeamManagementModelPopulator.populateModelByOrganisationId(
                    applicationId, organisationId, loggedInUser.getId());
            model.addAttribute("model", viewModel);
            addExistingApplicantsToForm(viewModel, form);
            return "application-team/edit-org";
        });
    }

    @PostMapping(params = {"updateOrganisation", "organisation"})
    public String submitUpdateOrganisation(Model model,
                                           @PathVariable("applicationId") Long applicationId,
                                           @RequestParam(name = "organisation") long organisationId,
                                           UserResource loggedInUser,
                                           @Valid @ModelAttribute(FORM_ATTR_NAME) ApplicationTeamUpdateForm form,
                                           @SuppressWarnings("unused") BindingResult bindingResult,
                                           ValidationHandler validationHandler) {
        return validateOrganisationAndApplicationIds(applicationId, organisationId, () -> {
            validateUniqueEmails(form, bindingResult);
            Supplier<String> failureView = () -> getUpdateOrganisation(model, applicationId, organisationId, loggedInUser, form);

            return validationHandler.failNowOrSucceedWith(failureView, () -> {
                ServiceResult<InviteResultsResource> updateResult = updateInvitesByOrganisation(organisationId, form, applicationId);

                return validationHandler.addAnyErrors(updateResult, fieldErrorsToFieldErrors(), asGlobalErrors())
                        .failNowOrSucceedWith(failureView, () -> format("redirect:/application/%s/team", applicationId));
            });
        }) ;
    }

    @PostMapping(params = {"addApplicant", "organisation"})
    public String addApplicant(Model model,
                               @PathVariable("applicationId") long applicationId,
                               @RequestParam(name = "organisation") long organisationId,
                               UserResource loggedInUser,
                               @ModelAttribute(FORM_ATTR_NAME) ApplicationTeamUpdateForm form) {
        return validateOrganisationAndApplicationIds(applicationId, organisationId, () -> {
            form.getApplicants().add(new ApplicantInviteForm());
            return getUpdateOrganisation(model, applicationId, organisationId, loggedInUser, form);
        });
    }

    @PostMapping(params = {"removeApplicant", "organisation"})
    public String removeApplicant(Model model,
                                  @PathVariable("applicationId") long applicationId,
                                  @RequestParam(name = "organisation") long organisationId,
                                  UserResource loggedInUser,
                                  @ModelAttribute(FORM_ATTR_NAME) ApplicationTeamUpdateForm form,
                                  @RequestParam(name = "removeApplicant") Integer position) {

        return validateOrganisationAndApplicationIds(applicationId, organisationId, () -> {
            form.getApplicants().remove(position.intValue());
            return getUpdateOrganisation(model, applicationId, organisationId, loggedInUser, form);
        });
    }

    @PostMapping(params = {"markForRemoval", "organisation"})
    public String markForRemoval(Model model,
                                 @PathVariable("applicationId") long applicationId,
                                 @RequestParam(name = "organisation") long organisationId,
                                 UserResource loggedInUser,
                                 @ModelAttribute(FORM_ATTR_NAME) ApplicationTeamUpdateForm form,
                                 @RequestParam(name = "markForRemoval") long applicationInviteId) {

        return validateOrganisationAndApplicationIds(applicationId, organisationId, () -> {
            form.getMarkedForRemoval().add(applicationInviteId);
            return getUpdateOrganisation(model, applicationId, organisationId, loggedInUser, form);
        });
    }

    @GetMapping(params = {"deleteOrganisation", "organisation"})
    public String confirmDeleteOrganisation(Model model,
                                            @PathVariable("applicationId") long applicationId,
                                            @RequestParam("organisation") long organisationId,
                                            UserResource loggedInUser) {

        return validateOrganisationAndApplicationIds(applicationId, organisationId, () -> {
            model.addAttribute("model", applicationTeamManagementModelPopulator.populateModelByOrganisationId(
                    applicationId,
                    organisationId,
                    loggedInUser.getId()
            ));

            return "application-team/delete-org";
        });
    }

    @PostMapping(params = {"deleteOrganisation", "organisation"})
    public String deleteOrganisation(Model model,
                                     @PathVariable("applicationId") Long applicationId,
                                     @RequestParam("organisation") long organisationId,
                                     UserResource loggedInUser,
                                     @Valid @ModelAttribute(FORM_ATTR_NAME) ApplicationTeamUpdateForm form) {

        return validateOrganisationAndApplicationIds(applicationId, organisationId, () -> {
            List<Long> existingApplicantIds = inviteOrganisationRestService.getByOrganisationIdWithInvitesForApplication(organisationId, applicationId)
                    .toOptionalIfNotFound()
                    .getSuccessObjectOrThrowException()
                    .map(organisation -> organisation.getInviteResources().stream()
                            .map(ApplicationInviteResource::getId)
                            .collect(Collectors.toList())
                    )
                    .orElse(Collections.emptyList());

            return processAnyFailuresOrSucceed(simpleMap(existingApplicantIds, applicationService::removeCollaborator))
                    .handleSuccessOrFailure(
                            failure -> getUpdateOrganisation(model, applicationId, organisationId, loggedInUser, form),
                            success -> format("redirect:/application/%s/team", applicationId)
                    );
        });
    }

    private ServiceResult<InviteResultsResource> updateInvitesByOrganisation(long organisationId,
                                                                             ApplicationTeamUpdateForm form,
                                                                             long applicationId) {
        List<ApplicationInviteResource> invites = createInvites(form, applicationId);

        return processAnyFailuresOrSucceed(simpleMap(form.getMarkedForRemoval(), applicationService::removeCollaborator))
                .andOnSuccess(() -> invites.isEmpty() ?
                        serviceSuccess(new InviteResultsResource()) :
                        inviteRestService.createInvitesByOrganisation(organisationId, invites).toServiceResult()
                );
    }

    private List<ApplicationInviteResource> createInvites(ApplicationTeamUpdateForm applicationTeamUpdateForm,
                                                          long applicationId) {
        return createInvites(applicationTeamUpdateForm, applicationId, null);
    }
}
