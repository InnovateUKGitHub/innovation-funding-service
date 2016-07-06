package com.worth.ifs.registration.service;

import com.worth.ifs.application.service.OrganisationService;
import com.worth.ifs.invite.resource.InviteOrganisationResource;
import com.worth.ifs.invite.resource.InviteResource;
import com.worth.ifs.user.resource.OrganisationResource;
import com.worth.ifs.user.resource.UserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

@Service
public class RegistrationServiceImpl implements RegistrationService {

    @Autowired
    OrganisationService organisationService;

    @Override
    public boolean invalidInvite(Model model, UserResource loggedInUser, InviteResource inviteResource, InviteOrganisationResource inviteOrganisation) {
        if (!inviteResource.getEmail().equals(loggedInUser.getEmail())) {
            // Invite is for different emailaddress then current logged in user.
            model.addAttribute("failureMessageKey", "registration.LOGGED_IN_WITH_OTHER_ACCOUNT");
            return true;
        } else if (inviteOrganisation.getOrganisation() != null && !inviteOrganisation.getOrganisation().equals(loggedInUser.getOrganisations().get(0))) {
            // Invite Organisation is already confirmed, with different organisation than the current users organisation.
            OrganisationResource userOrganisationResource = organisationService.getOrganisationByIdForAnonymousUserFlow(loggedInUser.getOrganisations().get(0));

            if(inviteOrganisation.getOrganisationNameConfirmed().equals(userOrganisationResource.getName())) {
                model.addAttribute("failureMessageKey", "registration.JOINING_SAME_ORGANISATIONS");
            } else {
                model.addAttribute("failureMessageKey", "registration.MULTIPLE_ORGANISATIONS");
            }

            return true;
        }
        return false;
    }
}
