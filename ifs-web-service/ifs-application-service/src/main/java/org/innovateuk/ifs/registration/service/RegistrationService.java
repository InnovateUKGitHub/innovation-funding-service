package org.innovateuk.ifs.registration.service;

import org.innovateuk.ifs.invite.resource.ApplicationInviteResource;
import org.innovateuk.ifs.invite.resource.InviteOrganisationResource;

/**
 * Interface for {@link RegistrationServiceImpl}
 */
public interface RegistrationService {

    boolean isInviteForDifferentOrganisationThanUsersAndDifferentName(ApplicationInviteResource inviteResource, InviteOrganisationResource inviteOrganisation);

    boolean isInviteForDifferentOrganisationThanUsersButSameName(ApplicationInviteResource inviteResource, InviteOrganisationResource inviteOrganisation);
}
