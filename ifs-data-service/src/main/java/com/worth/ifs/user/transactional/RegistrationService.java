package com.worth.ifs.user.transactional;

import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.security.NotSecured;
import com.worth.ifs.user.resource.UserResource;

/**
 * Transactional service around User operations
 */
public interface RegistrationService {

    @NotSecured("TODO - implement when permissions matrix defined")
    ServiceResult<UserResource> createUserLeadApplicantForOrganisation(Long organisationId, UserResource userResource);
}
