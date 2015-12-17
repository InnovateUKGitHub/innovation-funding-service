package com.worth.ifs.application.service;

import com.worth.ifs.application.resource.ApplicationResource;
import com.worth.ifs.commons.resource.ResourceEnvelope;
import com.worth.ifs.user.domain.User;
import com.worth.ifs.user.resource.UserResource;

import java.util.List;
import java.util.Set;

/**
 * Interface for CRUD operations on {@link User} related data.
 */
public interface UserService {
    List<User> getAssignable(Long applicationId);
    Boolean isLeadApplicant(Long userId, ApplicationResource application);
    public Set<User> getAssignableUsers(ApplicationResource application);
    public Set<User> getApplicationUsers(ApplicationResource application);
    public ResourceEnvelope<UserResource> createLeadApplicantForOrganisation(String firstName, String lastName, String password, String email, String title, String phoneNumber, Long organisationId);

}
