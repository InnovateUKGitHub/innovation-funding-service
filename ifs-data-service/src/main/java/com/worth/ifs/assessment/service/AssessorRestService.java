package com.worth.ifs.assessment.service;

import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.registration.resource.UserRegistrationResource;
import com.worth.ifs.user.domain.User;

/**
 * Interface for CRUD operations on {@link User} for assessor related data.
 */
public interface AssessorRestService {

    RestResult<Void> createAssessorByInviteHash(String hash, UserRegistrationResource userRegistrationResource);

}
