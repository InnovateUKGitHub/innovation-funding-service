package com.worth.ifs.user.service;

import com.worth.ifs.commons.resource.ResourceEnvelope;
import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.user.domain.ProcessRole;
import com.worth.ifs.user.domain.User;
import com.worth.ifs.user.resource.UserResource;

import java.util.List;

/**
 * Interface for CRUD operations on {@link User} related data.
 */
public interface UserRestService {
    User retrieveUserByToken(String token);
    User retrieveUserByEmailAndPassword(String email, String password);
    User retrieveUserById(Long id);

    List<User> findAll();
    RestResult<ProcessRole> findProcessRole(Long userId, Long applicationId);
    RestResult<List<ProcessRole>> findProcessRole(Long applicationId);
    List<User> findAssignableUsers(Long applicationId);
    List<UserResource> findUserByEmail(String email);
    RestResult<List<ProcessRole>> findAssignableProcessRoles(Long applicationId);
    List<User> findRelatedUsers(Long applicationId);
    RestResult<ProcessRole> findProcessRoleById(Long processRoleId);
    ResourceEnvelope<UserResource> createLeadApplicantForOrganisation(String firstName, String lastName, String password, String email, String title, String phoneNumber, Long organisationId);
    ResourceEnvelope<UserResource> updateDetails(String email, String firstName, String lastName, String title, String phoneNumber);
}
