package com.worth.ifs.assessment.transactional;

import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.registration.resource.UserRegistrationResource;
import org.springframework.security.access.prepost.PostAuthorize;

/**
 * Transactional and secured service providing operations around {@link com.worth.ifs.assessment.domain.Assessment} data.
 */
public interface AssessorService {

    @PostAuthorize("hasPermission(returnObject, 'CREATE')")
    ServiceResult<Void> registerAssessorByHash(String inviteHash, UserRegistrationResource userRegistrationResource);

}
