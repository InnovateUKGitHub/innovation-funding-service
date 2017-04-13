package org.innovateuk.ifs.user.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.user.resource.*;

import java.util.List;
import java.util.concurrent.Future;

/**
 * Interface for CRUD operations on {@link UserResource} related data.
 */
public interface UserRestService {
    RestResult<UserResource> retrieveUserResourceByUid(String uid);

    RestResult<UserResource> retrieveUserById(Long id);
    RestResult<List<UserResource>> findAll();

    RestResult<List<UserResource>> findByUserRoleType(UserRoleType userRoleType);

    RestResult<ProcessRoleResource> findProcessRole(Long userId, Long applicationId);
    RestResult<List<ProcessRoleResource>> findProcessRole(Long applicationId);
    RestResult<List<UserResource>> findAssignableUsers(Long applicationId);
    RestResult<UserResource> findUserByEmail(String email);
    Future<RestResult<ProcessRoleResource[]>> findAssignableProcessRoles(Long applicationId);
    RestResult<Boolean> userHasApplicationForCompetition(Long userId, Long competitionId);

    Future<RestResult<ProcessRoleResource>> findProcessRoleById(Long processRoleId);
    RestResult<Void> verifyEmail(String hash);
    RestResult<Void> resendEmailVerificationNotification(String email);
    RestResult<Void> sendPasswordResetNotification(String email);
    RestResult<Void> checkPasswordResetHash(String hash);
    RestResult<Void> resetPassword(String hash, String password);

    RestResult<UserResource> createLeadApplicantForOrganisationWithCompetitionId(String firstName, String lastName, String password, String email, String title,
                                                                                 String phoneNumber, String gender, Long ethnicity, String disability, Long organisationId, Long competitionId);
    RestResult<UserResource> createLeadApplicantForOrganisation(String firstName, String lastName, String password, String email, String title,
                                                                String phoneNumber, String gender, Long ethnicity, String disability, Long organisationId);
    RestResult<UserResource> updateDetails(Long id, String email, String firstName, String lastName, String title, String phoneNumber, String gender, Long ethnicity, String disability);
}
