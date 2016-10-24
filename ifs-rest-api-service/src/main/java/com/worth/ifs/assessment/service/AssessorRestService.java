package com.worth.ifs.assessment.service;

import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.registration.resource.UserRegistrationResource;

/**
 * Interface for CRUD operations on {@link com.worth.ifs.user.resource.UserResource} for assessor related data.
 */
public interface AssessorRestService {

    RestResult<Void> createAssessorByInviteHash(String hash, UserRegistrationResource userRegistrationResource);

}
