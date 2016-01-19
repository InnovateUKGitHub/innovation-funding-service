package com.worth.ifs.user.transactional;

import com.worth.ifs.security.NotSecured;
import com.worth.ifs.transactional.ServiceResult;
import com.worth.ifs.user.domain.User;
import com.worth.ifs.user.resource.UserResource;

/**
 * Transactional service around User operations
 */
public interface RegistrationService {

    @NotSecured("TODO - implement when permissions matrix defined")
    ServiceResult<User> createUserLeadApplicantForOrganisation(Long organisationId, UserResource userResource);
}
