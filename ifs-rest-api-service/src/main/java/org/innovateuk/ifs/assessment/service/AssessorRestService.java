package org.innovateuk.ifs.assessment.service;

import org.innovateuk.ifs.assessment.resource.AssessorProfileResource;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.registration.resource.UserRegistrationResource;

/**
 * Interface for CRUD operations on {@link org.innovateuk.ifs.user.resource.UserResource} for assessor related data.
 */
public interface AssessorRestService {

    RestResult<Void> createAssessorByInviteHash(String hash, UserRegistrationResource userRegistrationResource);

    RestResult<AssessorProfileResource> getAssessorProfile(Long assessorId);

    RestResult<Void> notifyAssessors(long competitionId);
}
