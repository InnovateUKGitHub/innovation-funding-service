package com.worth.ifs.user.service;

import com.worth.ifs.commons.resource.ResourceEnvelope;
import com.worth.ifs.user.domain.ProcessRole;
import com.worth.ifs.user.domain.User;
import com.worth.ifs.user.resource.UserResource;

import java.util.List;

/**
 * Interface for CRUD operations on {@link User} related data.
 */
public interface UserRestService {

    User retrieveUserByUid(String uid);
    User retrieveUserById(Long id);

    List<User> findAll();
    ProcessRole findProcessRole(Long userId, Long applicationId);
    List<ProcessRole> findProcessRole(Long applicationId);
    List<User> findAssignableUsers(Long applicationId);
    List<UserResource> findUserByEmail(String email);
    List<ProcessRole> findAssignableProcessRoles(Long applicationId);
    List<User> findRelatedUsers(Long applicationId);
    ProcessRole findProcessRoleById(Long processRoleId);
    ResourceEnvelope<UserResource> createLeadApplicantForOrganisation(String firstName, String lastName, String password, String email, String title, String phoneNumber, Long organisationId);
    ResourceEnvelope<UserResource> updateDetails(String email, String firstName, String lastName, String title, String phoneNumber);
}
