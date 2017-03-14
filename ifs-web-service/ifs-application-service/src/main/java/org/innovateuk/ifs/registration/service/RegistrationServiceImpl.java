package org.innovateuk.ifs.registration.service;

import org.innovateuk.ifs.application.service.OrganisationService;
import org.innovateuk.ifs.invite.resource.ApplicationInviteResource;
import org.innovateuk.ifs.invite.resource.InviteOrganisationResource;
import org.innovateuk.ifs.invite.resource.ApplicationInviteResource;
import org.innovateuk.ifs.user.resource.OrganisationResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.swing.text.html.Option;
import java.util.*;

import static java.util.Collections.singletonList;
import static java.util.Optional.empty;
import static java.util.Optional.of;

/**
 * Service to handle default actions on the registration/invite process
 */
@Service
public class RegistrationServiceImpl implements RegistrationService {

    @Autowired
    private OrganisationService organisationService;

    @Autowired
    private UserService userService;

    // qqRP TODO remove this?
    @Override
    public Map<String, String> getInvalidInviteMessages(UserResource loggedInUser, ApplicationInviteResource inviteResource, InviteOrganisationResource inviteOrganisation) {
        Map<String, String> modelAttributes = new HashMap<>();

        if (!inviteResource.getEmail().equalsIgnoreCase(loggedInUser.getEmail())) {
            modelAttributes.put("failureMessageKey", "registration.LOGGED_IN_WITH_OTHER_ACCOUNT");

        } else if (inviteOrganisation.getOrganisation() != null) {
            OrganisationResource userOrganisation = organisationService.getOrganisationForUser(loggedInUser.getId());
            if (!inviteOrganisation.getOrganisation().equals(userOrganisation.getId())) {
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

    @Override
    public List<String> validateInvite(ApplicationInviteResource inviteResource, InviteOrganisationResource inviteOrganisation) {
        Optional<UserResource> inviteUser = userService.findUserByEmailForAnonymousUserFlow(inviteResource.getEmail());
        List<String> errors = inviteUser.map(user -> {
                    OrganisationResource userOrganisation = organisationService.getOrganisationForUser(user.getId());
                    if (userOrganisation.getId().equals(inviteOrganisation.getOrganisation())) {
                        OrganisationResource userOrganisationResource = organisationService.getOrganisationByIdForAnonymousUserFlow(userOrganisation.getId());
                        if (inviteOrganisation.getOrganisationNameConfirmed().equalsIgnoreCase(userOrganisationResource.getName())) {
                            return singletonList("registration.JOINING_SAME_ORGANISATIONS");
                        } else {
                            return singletonList("registration.MULTIPLE_ORGANISATIONS");
                        }
                    }
                    return Collections.<String>emptyList();
                }
        ).orElse(Collections.<String>emptyList());
        return errors;
    }
}
