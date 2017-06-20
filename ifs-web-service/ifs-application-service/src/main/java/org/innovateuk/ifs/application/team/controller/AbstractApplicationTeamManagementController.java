package org.innovateuk.ifs.application.team.controller;

import org.innovateuk.ifs.application.team.form.ApplicantInviteForm;
import org.innovateuk.ifs.application.team.form.ApplicationTeamUpdateForm;
import org.innovateuk.ifs.application.team.viewmodel.ApplicationTeamManagementApplicantRowViewModel;
import org.innovateuk.ifs.application.team.viewmodel.ApplicationTeamManagementViewModel;
import org.innovateuk.ifs.commons.error.exception.ObjectNotFoundException;
import org.innovateuk.ifs.invite.resource.ApplicationInviteResource;
import org.innovateuk.ifs.invite.resource.InviteOrganisationResource;
import org.innovateuk.ifs.invite.service.InviteOrganisationRestService;
import org.innovateuk.ifs.user.resource.ProcessRoleResource;
import org.innovateuk.ifs.user.service.ProcessRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

import static java.lang.String.format;
import static org.innovateuk.ifs.util.CollectionFunctions.forEachWithIndex;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;

/**
 * This controller will handle all requests that are related to the management of application participants
 */
public abstract class AbstractApplicationTeamManagementController {

    protected static final String FORM_ATTR_NAME = "form";

    @Autowired
    private InviteOrganisationRestService inviteOrganisationRestService;

    @Autowired
    private ProcessRoleService processRoleService;

    protected List<ApplicationInviteResource> createInvites(ApplicationTeamUpdateForm applicationTeamUpdateForm,
                                                          long applicationId,
                                                          Long inviteOrganisationId) {
        return simpleMap(
                applicationTeamUpdateForm.getApplicants(),
                applicantInviteForm -> createInvite(applicantInviteForm, applicationId, inviteOrganisationId)
        );
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

    protected void validateUniqueEmails(ApplicationTeamUpdateForm form, BindingResult bindingResult) {
        Set<String> emails = new HashSet<>(form.getExistingApplicants());

        forEachWithIndex(form.getApplicants(), (index, applicantInviteForm) -> {
            if (!emails.add(applicantInviteForm.getEmail())) {
                bindingResult.rejectValue(format("applicants[%s].email", index), "email.already.in.invite",
                        "You have used this email address for another applicant.");
            }
        });
    }

    // Detecting duplicate applicants when the form is submitted requires the pre-population of existing applicants for reference.
    protected void addExistingApplicantsToForm(ApplicationTeamManagementViewModel viewModel, ApplicationTeamUpdateForm form) {
        List<ApplicationTeamManagementApplicantRowViewModel> applicants = viewModel.getApplicants();
        form.setExistingApplicants(simpleMap(applicants, row -> row.getEmail()));
    }


    protected String validateOrganisationAndApplicationIds(Long applicationId, Long organisationId, Supplier<String> supplier) {
        List<ProcessRoleResource> processRoles = processRoleService.getByApplicationId(applicationId);
        if (processRoles.stream().anyMatch(processRoleResource -> organisationId.equals(processRoleResource.getOrganisationId()))) {
            return supplier.get();
        }
        throw new ObjectNotFoundException("Organisation id not found in application id provided.", Collections.emptyList());
    }

    protected String validateOrganisationInviteAndApplicationId(Long applicationId, Long organisationInviteId, Supplier<String> supplier) {
        InviteOrganisationResource organisation = inviteOrganisationRestService.getById(organisationInviteId).getSuccessObjectOrThrowException();
        if(organisation.getInviteResources().stream().anyMatch(applicationInviteResource -> applicationInviteResource.getApplication().equals(applicationId))) {
            return supplier.get();
        }
        throw new ObjectNotFoundException("Organisation invite id not found in application id provided.", Collections.emptyList());
    }
}
