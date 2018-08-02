package org.innovateuk.ifs.registration.model;

import org.innovateuk.ifs.invite.resource.ApplicationInviteResource;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.registration.viewmodel.InviteAndUserOrganisationDifferentViewModel;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.service.OrganisationRestService;
import org.innovateuk.ifs.user.service.UserService;
import org.springframework.stereotype.Component;


@Component
public class InviteAndUserOrganisationDifferentModelPopulator {

    private OrganisationRestService organisationRestService;
    private UserService userService;

    public InviteAndUserOrganisationDifferentModelPopulator(OrganisationRestService organisationRestService,
                                                            UserService userService) {
        this.organisationRestService = organisationRestService;
        this.userService = userService;
    }

    public InviteAndUserOrganisationDifferentViewModel populateModel(ApplicationInviteResource invite) {
        String inviteOrganisationName = invite.getInviteOrganisationNameConfirmedSafe();
        UserResource user = userService.findUserByEmail(invite.getEmail()).get();
        OrganisationResource userOrganisation = organisationRestService.getOrganisationByUserId(user.getId()).getSuccess();
        String leadApplicantName = invite.getLeadApplicant();
        String leadApplicantEmail = invite.getLeadApplicantEmail();
        return new InviteAndUserOrganisationDifferentViewModel(inviteOrganisationName, userOrganisation.getName(), leadApplicantName, leadApplicantEmail);
    }
}
