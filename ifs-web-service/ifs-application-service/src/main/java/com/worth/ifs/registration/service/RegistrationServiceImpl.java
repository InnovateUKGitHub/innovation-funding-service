package com.worth.ifs.registration.service;

import com.worth.ifs.application.service.OrganisationService;
import com.worth.ifs.invite.resource.ApplicationInviteResource;
import com.worth.ifs.invite.resource.InviteOrganisationResource;
import com.worth.ifs.invite.resource.ApplicationInviteResource;
import com.worth.ifs.user.resource.OrganisationResource;
import com.worth.ifs.user.resource.UserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * Service to handle default actions on the registration/invite process
 */
@Service
public class RegistrationServiceImpl implements RegistrationService {

    @Autowired
    private OrganisationService organisationService;

    @Override
    public Map<String, String> getInvalidInviteMessages(UserResource loggedInUser, ApplicationInviteResource inviteResource, InviteOrganisationResource inviteOrganisation) {
        Map<String, String> modelAttributes = new HashMap<>();

        if (!inviteResource.getEmail().equalsIgnoreCase(loggedInUser.getEmail())) {
            // Invite is for different emailaddress then current logged in user.
            modelAttributes.put("failureMessageKey", "registration.LOGGED_IN_WITH_OTHER_ACCOUNT");
        } else if (inviteOrganisation.getOrganisation() != null && !inviteOrganisation.getOrganisation().equals(loggedInUser.getOrganisations().get(0))) {
            // Invite Organisation is already confirmed, with different organisation than the current users organisation.
            OrganisationResource userOrganisationResource = organisationService.getOrganisationByIdForAnonymousUserFlow(loggedInUser.getOrganisations().get(0));

            if(inviteOrganisation.getOrganisationNameConfirmed().equals(userOrganisationResource.getName())) {
                modelAttributes.put("failureMessageKey", "registration.JOINING_SAME_ORGANISATIONS");
            } else {
                modelAttributes.put("failureMessageKey", "registration.MULTIPLE_ORGANISATIONS");
            }

        }
        return modelAttributes;
    }
}
