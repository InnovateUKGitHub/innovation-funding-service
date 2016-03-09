package com.worth.ifs.user.service;

import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.user.domain.ProcessRole;
import com.worth.ifs.user.domain.User;
import com.worth.ifs.user.resource.UserResource;

import java.util.List;
import java.util.concurrent.Future;

/**
 * Interface for CRUD operations on {@link User} related data.
 */
public interface UserRestService {
    RestResult<User> retrieveUserByUid(String uid);
    RestResult<User> retrieveUserById(Long id);
    RestResult<List<User>> findAll();
    RestResult<ProcessRole> findProcessRole(Long userId, Long applicationId);
    RestResult<List<ProcessRole>> findProcessRole(Long applicationId);
    RestResult<List<User>> findAssignableUsers(Long applicationId);
    RestResult<List<UserResource>> findUserByEmail(String email);
    Future<RestResult<ProcessRole[]>> findAssignableProcessRoles(Long applicationId);
    RestResult<List<User>> findRelatedUsers(Long applicationId);
    Future<RestResult<ProcessRole>> findProcessRoleById(Long processRoleId);
    RestResult<Void> verifyEmail(String hash);
    RestResult<UserResource> createLeadApplicantForOrganisationWithCompetitionId(String firstName, String lastName, String password, String email, String title, String phoneNumber, Long organisationId, Long competitionId);
    RestResult<UserResource> createLeadApplicantForOrganisation(String firstName, String lastName, String password, String email, String title, String phoneNumber, Long organisationId);
    RestResult<UserResource> updateDetails(String email, String firstName, String lastName, String title, String phoneNumber);
}
