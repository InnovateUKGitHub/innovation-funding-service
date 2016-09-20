package com.worth.ifs.assessment.service;

import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.user.domain.User;
import com.worth.ifs.user.resource.UserResource;

/**
 * Interface for CRUD operations on {@link User} for assessor related data.
 */
public interface AssessorRestService {
    public RestResult<UserResource> createAssessorByInviteHash(String hash, String firstName, String lastName, String password, String email, String title, String phoneNumber, String gender, String disability, Long ethnicity);
}
