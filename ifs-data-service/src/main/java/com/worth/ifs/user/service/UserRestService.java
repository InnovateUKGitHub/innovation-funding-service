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
    public User retrieveUserByToken(String token);
    public User retrieveUserByEmailAndPassword(String email, String password);
    public User retrieveUserById(Long id);

    public List<User> findAll();
    public ProcessRole findProcessRole(Long userId, Long applicationId);
    public List<ProcessRole> findProcessRole(Long applicationId);
    public List<User> findAssignableUsers(Long applicationId);
    public List<ProcessRole> findAssignableProcessRoles(Long applicationId);
    public List<User> findRelatedUsers(Long applicationId);
    public ProcessRole findProcessRoleById(Long processRoleId);
    public ResourceEnvelope<UserResource> createUserForOrganisationWithRole(String firstName, String lastName, String password, String email, String title, String phoneNumber, Long organisationId, String roleName);
}
