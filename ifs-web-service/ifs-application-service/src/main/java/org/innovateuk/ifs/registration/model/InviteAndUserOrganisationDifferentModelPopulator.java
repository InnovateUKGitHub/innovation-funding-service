package org.innovateuk.ifs.registration.model;

import org.innovateuk.ifs.application.service.OrganisationService;
import org.innovateuk.ifs.invite.resource.ApplicationInviteResource;
import org.innovateuk.ifs.registration.viewmodel.InviteAndUserOrganisationDifferentViewModel;
import org.innovateuk.ifs.user.resource.OrganisationResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class InviteAndUserOrganisationDifferentModelPopulator {

    @Autowired
    private OrganisationService organisationService;

    @Autowired
    private UserService userService;

    public InviteAndUserOrganisationDifferentViewModel populateModel(ApplicationInviteResource invite) {
        String inviteOrganisationName = invite.getInviteOrganisationNameConfirmedSafe();
        UserResource user = userService.findUserByEmail(invite.getEmail()).get();
        OrganisationResource userOrganisation = organisationService.getOrganisationForUser(user.getId());
        String leadApplicantName = invite.getLeadApplicant();
        String leadApplicantEmail = invite.getLeadApplicantEmail();
        return new InviteAndUserOrganisationDifferentViewModel(inviteOrganisationName, userOrganisation.getName(), leadApplicantName, leadApplicantEmail);
    }
}
