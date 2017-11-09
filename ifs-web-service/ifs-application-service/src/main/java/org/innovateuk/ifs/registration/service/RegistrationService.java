package org.innovateuk.ifs.registration.service;

import org.innovateuk.ifs.commons.security.NotSecured;
import org.innovateuk.ifs.invite.resource.ApplicationInviteResource;
import org.innovateuk.ifs.invite.resource.InviteOrganisationResource;
import org.innovateuk.ifs.user.resource.UserResource;

import java.util.List;
import java.util.Map;

/**
 * Interface for {@link RegistrationServiceImpl}
 */
public interface RegistrationService {

    @NotSecured(value = "Not currently secured", mustBeSecuredByOtherServices = false)
    boolean isInviteForDifferentOrganisationThanUsersAndDifferentName(ApplicationInviteResource inviteResource, InviteOrganisationResource inviteOrganisation);

    @NotSecured(value = "Not currently secured", mustBeSecuredByOtherServices = false)
    boolean isInviteForDifferentOrganisationThanUsersButSameName(ApplicationInviteResource inviteResource, InviteOrganisationResource inviteOrganisation);
}
