package org.innovateuk.ifs.registration.service;

import org.innovateuk.ifs.invite.resource.ApplicationInviteResource;
import org.innovateuk.ifs.invite.resource.InviteOrganisationResource;
import org.innovateuk.ifs.user.resource.UserResource;

import java.util.List;
import java.util.Map;

/**
 * Interface for {@link RegistrationServiceImpl}
 */
public interface RegistrationService {

    Map<String, String> getInvalidInviteMessages(UserResource loggedInUser, ApplicationInviteResource inviteResource, InviteOrganisationResource inviteOrganisation);

    List<String> validateInvite(ApplicationInviteResource inviteResource, InviteOrganisationResource inviteOrganisation);
}
