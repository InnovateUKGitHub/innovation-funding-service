package org.innovateuk.ifs.registration.service;

import org.innovateuk.ifs.application.service.OrganisationService;
import org.innovateuk.ifs.invite.resource.ApplicationInviteResource;
import org.innovateuk.ifs.invite.resource.InviteOrganisationResource;
import org.innovateuk.ifs.invite.resource.ApplicationInviteResource;
import org.innovateuk.ifs.user.resource.OrganisationResource;
import org.innovateuk.ifs.user.resource.UserResource;
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
            // Invite is for different email address than current logged in user.
            modelAttributes.put("failureMessageKey", "registration.LOGGED_IN_WITH_OTHER_ACCOUNT");
        } else if (inviteOrganisation.getOrganisation() != null) {
            OrganisationResource userOrganisation = organisationService.getOrganisationForUser(loggedInUser.getId());
            if (!inviteOrganisation.getOrganisation().equals(userOrganisation.getId())) {
                // Invite Organisation is already confirmed, with different organisation than the current users organisation.
                OrganisationResource userOrganisationResource = organisationService.getOrganisationByIdForAnonymousUserFlow(userOrganisation.getId());

                if (inviteOrganisation.getOrganisationNameConfirmed().equals(userOrganisationResource.getName())) {
                    modelAttributes.put("failureMessageKey", "registration.JOINING_SAME_ORGANISATIONS");
                } else {
                    modelAttributes.put("failureMessageKey", "registration.MULTIPLE_ORGANISATIONS");
                }
            }
        }
        return modelAttributes;
    }
}
