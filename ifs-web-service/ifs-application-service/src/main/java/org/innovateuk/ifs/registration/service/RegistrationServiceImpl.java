package org.innovateuk.ifs.registration.service;

import org.innovateuk.ifs.application.service.OrganisationService;
import org.innovateuk.ifs.invite.resource.ApplicationInviteResource;
import org.innovateuk.ifs.invite.resource.InviteOrganisationResource;
import org.innovateuk.ifs.user.resource.OrganisationResource;
import org.innovateuk.ifs.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Service to handle default actions on the registration/invite process
 */
@Service
public class RegistrationServiceImpl implements RegistrationService {

    @Autowired
    private OrganisationService organisationService;

    @Autowired
    private UserService userService;

    @Override
    public boolean isInviteForDifferentOrganisationThanUsersAndDifferentName(ApplicationInviteResource invite, InviteOrganisationResource inviteOrganisation){
        return userService.findUserByEmail(invite.getEmail()).map(user -> {
            OrganisationResource userOrganisation = organisationService.getOrganisationForUser(user.getId()); // Will exist as the user does
            Long inviteOrganisationId = inviteOrganisation.getOrganisation(); // Can be null for new orgs
            if (inviteOrganisationId != null && !userOrganisation.getId().equals(inviteOrganisation.getOrganisation())){
                if (!userOrganisation.getName().equalsIgnoreCase(inviteOrganisation.getOrganisationNameConfirmed())){
                    return true;
                }
            }
            return false;
        }).orElse(false);
    }

    @Override
    public boolean isInviteForDifferentOrganisationThanUsersButSameName(ApplicationInviteResource invite, InviteOrganisationResource inviteOrganisation){
        return userService.findUserByEmail(invite.getEmail()).map(user -> {
            OrganisationResource userOrganisation = organisationService.getOrganisationForUser(user.getId()); // Will exist as the user does
            Long inviteOrganisationId = inviteOrganisation.getOrganisation(); // Can be null for new orgs
            if (inviteOrganisationId != null && !userOrganisation.getId().equals(inviteOrganisation.getOrganisation())){
                if (userOrganisation.getName().equalsIgnoreCase(inviteOrganisation.getOrganisationNameConfirmed())){
                    return true;
                }
            }
            return false;
        }).orElse(false);
    }
}
